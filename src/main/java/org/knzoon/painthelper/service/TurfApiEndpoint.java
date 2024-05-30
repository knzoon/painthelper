package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.model.dto.WardedDataDTO;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.knzoon.painthelper.representation.turfapi.IdParameter;
import org.knzoon.painthelper.representation.turfapi.UserInfoFromTurfApi;
import org.knzoon.painthelper.representation.turfapi.UserMinimal;
import org.knzoon.painthelper.representation.turfapi.ZoneFeedItemPart;
import org.knzoon.painthelper.representation.turfapi.ZoneMinimal;
import org.knzoon.painthelper.representation.turfapi.NameParameter;
import org.knzoon.painthelper.representation.warded.UniqueWardedZones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TurfApiEndpoint {

    private Logger logger = LoggerFactory.getLogger(TurfApiEndpoint.class);
    private final RestTemplate restTemplate;

    public TurfApiEndpoint() {
        this.restTemplate = new RestTemplate();
    }


    public List<ZoneFeedItemPart> fetchZonesFromTurfApi(List<String> zoneNames) {

        final String baseUrl = "https://api.turfgame.com/unstable/zones";
        URI uri = null;

        try {
            uri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            return Collections.emptyList();
        }

        List<NameParameter> zoneNameParameters = zoneNames.stream().map(NameParameter::new).collect(Collectors.toList());
        HttpEntity<List<NameParameter>> request = new HttpEntity<>(zoneNameParameters, getHttpHeaders());

        ResponseEntity<List<ZoneFeedItemPart>> result = restTemplate.exchange(uri, HttpMethod.POST, request, new ParameterizedTypeReference<List<ZoneFeedItemPart>>() {});

        List<ZoneFeedItemPart> fetchedZones = result.getBody();
        logger.info("Status från api-anrop {} ", result.getStatusCode());
        logger.info("Antal zoner från api: {}", fetchedZones.size());

        // TODO Add validation on call to TurfApi
        return fetchedZones;
    }



    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Long getUserId(WardedDataDTO wardedDataDTO) {
        final String baseUrl = "https://api.turfgame.com/unstable/users";
        URI uri = null;

        try {
            uri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            // TODO implement better error handling
            return -1L;
        }

        logger.info("Username to fetch user for: {}", wardedDataDTO.getUsername());

        List<NameParameter> nameParameters = Arrays.asList(new NameParameter(wardedDataDTO.getUsername()));
        HttpEntity<List<NameParameter>> request = new HttpEntity<>(nameParameters, getHttpHeaders());

        ResponseEntity<List<UserMinimal>> result = restTemplate.exchange(uri, HttpMethod.POST, request, new ParameterizedTypeReference<List<UserMinimal>>() {});
        List<UserMinimal> fetchedUsers = result.getBody();

        return fetchedUsers.get(0).getId();
    }

    public List<UserInfoFromTurfApi> getUserInfo(List<IdParameter> users) {
        final String baseUrl = "https://api.turfgame.com/unstable/users";
        URI uri = null;

        try {
            uri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            // TODO implement better error handling
            return List.of();
        }

        HttpEntity<List<IdParameter>> request = new HttpEntity<>(users, getHttpHeaders());
        logger.info("Trying to call turf api");
        ResponseEntity<List<UserInfoFromTurfApi>> result = restTemplate.exchange(uri, HttpMethod.POST, request, new ParameterizedTypeReference<List<UserInfoFromTurfApi>>() {});

        if (result.getStatusCode().isError()) {
            logger.info("Errorcode is {}", result.getStatusCode().value());

            String userIdListString = users.stream().map(IdParameter::getId).map(Objects::toString).collect(Collectors.joining());
            logger.info("List of userId: {}", userIdListString);
            return List.of();
        }

        return result.getBody();
    }


    public List<FeedItem> readFeed(FeedInfo feedInfo) {
        String dateTimeStr = feedInfo.getLatestFeedItemRead().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        String baseUrl = "https://api.turfgame.com/unstable/feeds/" + feedInfo.getFeedName();
        String uriString = null;
        URI uri = null;

        try {
            uriString = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParam("afterDate", URLEncoder.encode(dateTimeStr, "UTF-8")).build().toUriString();
//            logger.info("uriString: {}", uriString);
        } catch (UnsupportedEncodingException e) {
            return Collections.emptyList();
        }

        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            return Collections.emptyList();
        }


        HttpEntity<?> entity = new HttpEntity<>(getHttpHeaders());

        ResponseEntity<List<FeedItem>> result = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<List<FeedItem>>() {});

        List<FeedItem> feed = result.getBody();
//        logger.info("Status från api-anrop {} ", result.getStatusCode());
//        logger.info("Antal feedItems från api: {}", feed.size());

        return feed;
    }

    public List<ZoneFeedItemPart> getAllZones() {
        String baseUrl = "https://api.turfgame.com/unstable/zones/all";
        URI uri = null;

        try {
            uri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            return Collections.emptyList();
        }

        HttpEntity<?> entity = new HttpEntity<>(getHttpHeaders());
        logger.info("Försöker hämta alla zoner som finns från Turf-api");
        ResponseEntity<List<ZoneFeedItemPart>> result = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<List<ZoneFeedItemPart>>() {});

        List<ZoneFeedItemPart> fetchedZones = result.getBody();
        logger.info("Status från api-anrop {} ", result.getStatusCode());
        logger.info("Antal zoner från api: {}", fetchedZones.size());


        return fetchedZones;
    }


}
