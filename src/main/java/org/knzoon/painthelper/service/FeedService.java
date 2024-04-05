package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.*;
import org.knzoon.painthelper.model.feed.ImportFeedResult;
import org.knzoon.painthelper.model.feed.ImportFeedResultTotal;
import org.knzoon.painthelper.representation.feed.FeedReadResultRepresentation;
import org.knzoon.painthelper.representation.feed.SingleFeedImportResultRepresentation;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.knzoon.painthelper.representation.turfapi.UserMinimal;
import org.knzoon.painthelper.representation.turfapi.ZoneFeedItemPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.knzoon.painthelper.model.FeedInfo.TAKEOVER_FEED;
import static org.knzoon.painthelper.model.FeedInfo.ZONE_FEED;

@Component
public class FeedService {

    private final TurfApiEndpoint turfApiEndpoint;
    private final FeedInfoRepository feedInfoRepository;
    private final FeedReadInfoRepository feedReadInfoRepository;
    private final UserRepository userRepository;
    private final RegionTakesRepository regionTakesRepository;
    private final UniqueZoneRepository uniqueZoneRepository;
    private final ZoneRepository zoneRepository;
    private final ZoneUpdatedRepository zoneUpdatedRepository;
    private final TakeoverRepository takeoverRepository;
    private final RoundCalculator roundCalculator;
    private final ErrorTakeoverRepository errorTakeoverRepository;

    private final TakeoverFeedImporter takeoverFeedImporter;

    private Logger logger = LoggerFactory.getLogger(FeedService.class);

    @Autowired
    public FeedService(TurfApiEndpoint turfApiEndpoint, FeedInfoRepository feedInfoRepository, FeedReadInfoRepository feedReadInfoRepository,
                       UserRepository userRepository, RegionTakesRepository regionTakesRepository, UniqueZoneRepository uniqueZoneRepository,
                       ZoneRepository zoneRepository, ZoneUpdatedRepository zoneUpdatedRepository, TakeoverRepository takeoverRepository,
                       RoundCalculator roundCalculator, ErrorTakeoverRepository errorTakeoverRepository, TakeoverFeedImporter takeoverFeedImporter) {
        this.turfApiEndpoint = turfApiEndpoint;
        this.feedInfoRepository = feedInfoRepository;
        this.feedReadInfoRepository = feedReadInfoRepository;
        this.userRepository = userRepository;
        this.regionTakesRepository = regionTakesRepository;
        this.uniqueZoneRepository = uniqueZoneRepository;
        this.zoneRepository = zoneRepository;
        this.zoneUpdatedRepository = zoneUpdatedRepository;
        this.takeoverRepository = takeoverRepository;
        this.roundCalculator = roundCalculator;
        this.errorTakeoverRepository = errorTakeoverRepository;
        this.takeoverFeedImporter = takeoverFeedImporter;
    }

    @Transactional
    public void readFeedAndImportData(String feedName) {
        if (notValidFeed(feedName)) {
            return;
        }

        FeedInfo feedInfo = getFeedOrCreateIfNeccecery(feedName);
        List<FeedItem> allFeedItems = turfApiEndpoint.readFeed(feedInfo);


        ZonedDateTime readTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));

        if (allFeedItems.isEmpty()) {
            FeedReadInfo feedReadInfo = new FeedReadInfo(feedInfo.getId(), readTime, 0, 0);
            feedReadInfoRepository.save(feedReadInfo);
            return;
        }

        ZonedDateTime latestFeedItemRead = allFeedItems.get(0).getTime();
        feedInfo.setLatestFeedItemRead(latestFeedItemRead);

        Collections.reverse(allFeedItems);
        List<FeedItem> feedItems;

        if (TAKEOVER_FEED.equals(feedInfo.getFeedName())) {
            allFeedItems.stream().forEach(this::actionsConcerningAllFeedItems);
            feedItems = getFeedItemsOfInterest(allFeedItems, feedInfo);
        } else if (ZONE_FEED.equals(feedInfo.getFeedName())) {
            feedItems = allFeedItems;
        } else {
            return;
        }

        FeedReadInfo feedReadInfo = new FeedReadInfo(feedInfo.getId(), readTime, allFeedItems.size(), feedItems.size());
        feedItems.stream().forEach(this::importDataFromFeedItem);

        feedInfoRepository.save(feedInfo);
        feedReadInfoRepository.save(feedReadInfo);
    }

    private void actionsConcerningAllFeedItems(FeedItem feedItem) {
        handlePossibleUpdatedZone(feedItem);
        saveTakeovers(feedItem);
    }

    private void saveTakeovers(FeedItem feedItem) {
        User takeoverUser = getTakeoverUser(feedItem);
        List<User> assistingUsers = getAssistingUsers(feedItem);
        User previousUser = getPreviousUser(feedItem);

        saveTakeover(feedItem, takeoverUser, previousUser, TakeoverType.TAKEOVER, null);
        assistingUsers.stream().forEach(user -> saveTakeover(feedItem, user, previousUser, TakeoverType.ASSIST, takeoverUser));
    }

    private User getTakeoverUser(FeedItem feedItem) {
        return getOrCreateUser(feedItem.getTakeoverUser());
    }

    private User getOrCreateUser(UserMinimal userFromFeed) {
        User user;

        Optional<User> userInDB = userRepository.findById(userFromFeed.getId());

        if (userInDB.isPresent()) {
            user = userInDB.get();
            if (!user.getUsername().equals(userFromFeed.getName())) {
                user.setUsername(userFromFeed.getName());
            }
        } else {
            user = new User(userFromFeed.getId(), userFromFeed.getName());
            user = userRepository.save(user);

        }

        return user;
    }

    private List<User> getAssistingUsers(FeedItem feedItem) {
        if (feedItem.getAssists() == null) {
            return Collections.emptyList();
        }

        return feedItem.getAssists().stream().map(this::getOrCreateUser).collect(Collectors.toList());
    }

    private User getPreviousUser(FeedItem feedItem) {
        if (feedItem.getPreviousUser() == null) {
            return null;
        }

        return getOrCreateUser(feedItem.getPreviousUser());
    }

    private void saveTakeover(FeedItem feedItem, User currentUser, User previousUser, TakeoverType takeoverType, User assistingUser) {
        int roundId = roundCalculator.roundFromDateTime(feedItem.getTime());

        if (TakeoverType.TAKEOVER.equals(takeoverType) && previousUser != null && !previousUser.getId().equals(currentUser.getId())) {
            updatePreviousTakeOnZone(feedItem, currentUser, previousUser, roundId);
        }

        Takeover takeover = new Takeover(roundId, takeoverType, feedItem.getZoneId(), feedItem.getZoneType(), feedItem.getTime(), currentUser,
                feedItem.getPointsPerHour(), feedItem.getTakeoverPoints(), previousUser, assistingUser);
        takeoverRepository.save(takeover);
    }

    private void updatePreviousTakeOnZone(FeedItem feedItem, User currentUser, User previousUser, int roundId) {
        List<Takeover> previousTakeoverForZone = takeoverRepository.findPreviousTakeoverForZone(roundId, feedItem.getZoneId(), previousUser.getId());

        if (!previousTakeoverForZone.isEmpty()) {
            previousTakeoverForZone.get(0).setNextInfo(currentUser, feedItem.getTime());
        }
    }

    private void handlePossibleUpdatedZone(FeedItem feedItem) {
        Optional<Zone> possibleZoneFromDB = zoneRepository.findById(feedItem.getZone().getId());

        if (possibleZoneFromDB.isPresent()) {
            Zone zoneFromDB = possibleZoneFromDB.get();
            if (!feedItem.equalsExistingZone(zoneFromDB)) {
                zoneFromDB.updateNameOrCoordinates(feedItem.getZone().getName(), feedItem.getZone().getLatitude(), feedItem.getZone().getLongitude());
                updateUniqueZones(feedItem);
                logUpdateInDB(zoneFromDB.getId());
            }
        }
    }

    private void updateUniqueZones(FeedItem feedItem) {
        ZoneFeedItemPart zonefromFeed = feedItem.getZone();
        Iterable<UniqueZone> searchResult = uniqueZoneRepository.findByZoneZoneId(zonefromFeed.getId());
        StreamSupport.stream(searchResult.spliterator(), false).forEach(uz -> uz.updateNameOrCoordinates(zonefromFeed.getName(), zonefromFeed.getLatitude(), zonefromFeed.getLongitude()));
    }

    private void logUpdateInDB(Long zoneId) {
        zoneUpdatedRepository.save(new ZoneUpdated(zoneId, ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"))));
    }

    private boolean notValidFeed(String feedName) {
        return !TAKEOVER_FEED.equals(feedName) && !ZONE_FEED.equals(feedName);
    }


    private List<FeedItem> getFeedItemsOfInterest(List<FeedItem> allFeedItems, FeedInfo feedInfo) {
        if (TAKEOVER_FEED.equals(feedInfo.getFeedName())) {
            Map<Long, UserIdView> mapOfUsers = getImportedUsers();

            return allFeedItems.stream().filter(f -> userExists(f, mapOfUsers)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private Map<Long, UserIdView> getImportedUsers() {
        List<UserIdView> usersOfInterest = regionTakesRepository.findDistinctUsers();
        return usersOfInterest.stream().collect(Collectors.toMap(UserIdView::getUserId, Function.identity()));
    }

    private boolean userExists(FeedItem feedItem, Map<Long, UserIdView> mapOfUsers) {
        List<Long> allUserIdInvolved = feedItem.getAllUserIdInvolved();
        return allUserIdInvolved.stream().anyMatch(id -> mapOfUsers.containsKey(id));
    }

    private void importDataFromFeedItem(FeedItem feedItem) {
        if (TAKEOVER_FEED.equals(feedItem.getType())) {
            importTakeoverFeedItem(feedItem);
        } else if (ZONE_FEED.equals(feedItem.getType())) {
            importZoneFeedItem(feedItem);
        }
    }

    private void importTakeoverFeedItem(FeedItem feedItem) {
        List<Long> allUserIdInvolved = feedItem.getAllUserIdInvolved();
        Long regionId = feedItem.getRegionId();
        ZoneFeedItemPart zone = feedItem.getZone();
        ZonedDateTime takeoverTime = feedItem.getTime();

        allUserIdInvolved.stream().forEach(id -> importDataForSpecificUser(id, regionId, zone, takeoverTime));
    }

    private void importZoneFeedItem(FeedItem feedItem) {
        ZoneFeedItemPart zoneFromApi = feedItem.getZone();
        Optional<Zone> zoneInDB = zoneRepository.findById(zoneFromApi.getId());
        if (!zoneInDB.isPresent()) {
            zoneRepository.save(createZone(zoneFromApi));
            logger.info("Imported zone: {}, {} from feed", zoneFromApi.getName(), zoneFromApi.getRegionName());
        }
    }

    private void importDataForSpecificUser(Long userId, Long regionId, ZoneFeedItemPart zone, ZonedDateTime takeoverTime) {

        try {
            RegionTakes regionTakes = regionTakesRepository.findByUserIdAndRegionId(userId, regionId);

            if (regionTakes != null) {
                updateUniqueZone(zone, regionTakes);
            } else if (userHasBeenImported(userId)) {
                createNewRegionTakes(zone.getRegionName(), regionId, userId);
                regionTakes = regionTakesRepository.findByUserIdAndRegionId(userId, regionId);
                updateUniqueZone(zone, regionTakes);
            }
            
        } catch (Exception e) {
            logger.error("Failed to update unique zone for userId: {}, zoneId: {}", userId, zone.getId());
            errorTakeoverRepository.save(new ErrorTakeover(userId, zone.getId(), takeoverTime, ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"))));
        }
    }

    private void updateUniqueZone(ZoneFeedItemPart zone, RegionTakes regionTakes) {
        UniqueZone uniqueZone = getUniqueZoneOrCreateIfNeccecery(regionTakes, zone);
        uniqueZone.addTake();
        uniqueZoneRepository.save(uniqueZone);
    }

    private void createNewRegionTakes(String regionName, Long regionId, Long userId) {
        RegionTakes newRegionTakes = new RegionTakes(regionName, regionId, userId);
        regionTakesRepository.save(newRegionTakes);
        logger.info("Skapar en regionsdatahållare för userId {} och region {}", userId, regionName);
    }

    private boolean userHasBeenImported(Long userId) {
        return getImportedUsers().get(userId) != null;
    }

    private UniqueZone getUniqueZoneOrCreateIfNeccecery(RegionTakes regionTakes, ZoneFeedItemPart zoneFromFeed) {
        UniqueZone uniqueZone = uniqueZoneRepository.findByRegionTakesIdAndZoneZoneId(regionTakes.getId(), zoneFromFeed.getId());

        if (uniqueZone == null) {
            ZoneInfo zone = new ZoneInfo(zoneFromFeed.getId(), zoneFromFeed.getName(), zoneFromFeed.getLongitude(), zoneFromFeed.getLatitude(), zoneFromFeed.getAreaId(), zoneFromFeed.getAreaName());
            uniqueZone = new UniqueZone(0, zone, regionTakes);
        }

        return uniqueZone;
    }

    private FeedInfo getFeedOrCreateIfNeccecery(String feedName) {
        FeedInfo feedInfo = feedInfoRepository.findByFeedName(feedName);

        if (feedInfo == null) {
            feedInfo = new FeedInfo(feedName);
            feedInfoRepository.save(feedInfo);
        }

        return feedInfo;
    }

    @Transactional(timeout = 600)
    public int importAllZonesFromWarded() {
        logger.info("starting importing all zones from turf-api");
        List<ZoneFeedItemPart> allZones = turfApiEndpoint.getAllZones();

        if (allZones.isEmpty()) {
            return 0;
        }

        logger.info("removing any zones imported earlier");
        zoneRepository.deleteAll();
        logger.info("start parsing returned zones");
        List<Zone> zonesToPersist = allZones.stream().map(this::createZone).collect(Collectors.toList());
        logger.info("persist all zones at once");
        zoneRepository.saveAll(zonesToPersist);

        return zonesToPersist.size();
    }

    private Zone createZone(ZoneFeedItemPart zoneFromApi) {
        return new Zone(zoneFromApi.getId(), zoneFromApi.getName(), zoneFromApi.getLatitude(), zoneFromApi.getLongitude(),
                zoneFromApi.getRegionId(), zoneFromApi.getRegionName(), zoneFromApi.getAreaId(), zoneFromApi.getAreaName(),
                zoneFromApi.getCountryCode());
    }

    public FeedReadResultRepresentation readFromInternalFeed() {
        ImportFeedResultTotal importFeedResultTotal = new ImportFeedResultTotal();
        ImportFeedResult currentImportFeedResult;
//        logger.info("started feed read");
        int testdrive = 0;

        UUID lastReadFeedItemId = takeoverFeedImporter.findLastReadFeedItemId();

        do {
            testdrive++;
            currentImportFeedResult = takeoverFeedImporter.importFeedFrom(lastReadFeedItemId);

            importFeedResultTotal.addImportFeedResult(currentImportFeedResult);

            lastReadFeedItemId = currentImportFeedResult.getLastReadFeedItemId().orElse(lastReadFeedItemId);
//            logger.info("Time spent for feed {}: {}", testdrive, currentImportFeedResult.timeSpent());

//        } while (currentImportFeedResult.feedItemsRead());
        } while (testdrive < 5 && currentImportFeedResult.hasFeedItemsBeenRead());

//        logger.info("finished feed read");

        List<SingleFeedImportResultRepresentation> feedImportResultRepresentations = importFeedResultTotal.getImportFeedResults()
                .stream()
                .map(this::toRepresentation).collect(Collectors.toList());
        return new FeedReadResultRepresentation(importFeedResultTotal.getNumberOfFeedsReads(),
                importFeedResultTotal.totalTakeoversCreated(),
                importFeedResultTotal.totalUniqueZonesUpdated(),
                importFeedResultTotal.totalTimeSpent().toString(),
                feedImportResultRepresentations);
    }

    private SingleFeedImportResultRepresentation toRepresentation(ImportFeedResult importFeedResult) {
        return new SingleFeedImportResultRepresentation(importFeedResult.feedItemsRead(),
                importFeedResult.takeoversCreated(),
                importFeedResult.uniqueZonesUpdated(),
                importFeedResult.timeSpent().toString());
    }

}
