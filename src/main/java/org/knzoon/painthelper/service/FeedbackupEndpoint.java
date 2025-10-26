package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import java.util.Collections;
import java.util.List;

@Component
public class FeedbackupEndpoint {
    private Logger logger = LoggerFactory.getLogger(FeedbackupEndpoint.class);

    private final RestTemplate restTemplate;

    @Value("${feed.read.baseurl:https://feed.knzoon.se/feed/}")
    private String baseURL;

    public FeedbackupEndpoint() {
        this.restTemplate = new RestTemplate();
    }

    public List<FeedItem> readFeed(FeedInfo feedInfo) {
        String dateTimeStr = feedInfo.getLatestFeedItemRead().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
//        logger.info("dateTimeStr: {}", dateTimeStr);
        String feedURL = getBaseURL() + feedInfo.getFeedName();
//        logger.info("baseURl:  {}", feedURL);
        String uriString;
        URI uri;

        try {
            uriString = UriComponentsBuilder.fromHttpUrl(feedURL).queryParam("after", URLEncoder.encode(dateTimeStr, "UTF-8")).build().toUriString();
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
//        logger.info("Antal feedItems av typ {} från api: {}", feedInfo.getFeedName(), feed.size());

        return feed;
    }

    private String getBaseURL() {
        return baseURL != null && !baseURL.trim().isEmpty()? baseURL : "https://feed.knzoon.se/feed/";
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
