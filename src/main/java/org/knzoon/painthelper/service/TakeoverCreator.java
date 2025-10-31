package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.TakeoverRepository;
import org.knzoon.painthelper.model.TakeoverType;
import org.knzoon.painthelper.model.User;
import org.knzoon.painthelper.model.UserRepository;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.knzoon.painthelper.representation.turfapi.UserMinimal;
import org.knzoon.painthelper.util.RoundCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TakeoverCreator {

    private final UserRepository userRepository;
    private final TakeoverRepository takeoverRepository;

    @Autowired
    public TakeoverCreator(UserRepository userRepository, TakeoverRepository takeoverRepository) {
        this.userRepository = userRepository;
        this.takeoverRepository = takeoverRepository;
    }

    public int saveTakeovers(FeedItem feedItem) {
        User takeoverUser = getOrCreateUser(feedItem.getTakeoverUser());
        List<User> assistingUsers = getAssistingUsers(feedItem);
        User previousUser = getPreviousUser(feedItem);

        saveTakeover(feedItem, takeoverUser, previousUser, TakeoverType.TAKEOVER, null);
        assistingUsers.stream().forEach(user -> saveTakeover(feedItem, user, previousUser, TakeoverType.ASSIST, takeoverUser));
        return 1 + assistingUsers.size();
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
        int roundId = RoundCalculator.roundFromDateTime(feedItem.getTime());

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

}
