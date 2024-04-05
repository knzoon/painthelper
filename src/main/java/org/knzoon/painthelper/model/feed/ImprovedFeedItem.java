package org.knzoon.painthelper.model.feed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.knzoon.painthelper.representation.turfapi.FeedItem;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
public class ImprovedFeedItem {
    @Id
    private UUID id;

    private Long orderNumber;

    private ZonedDateTime takeoverTime;

    private String originalTakeover;

    public ImprovedFeedItem() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public ZonedDateTime getTakeoverTime() {
        return takeoverTime;
    }

    public void setTakeoverTime(ZonedDateTime takeoverTime) {
        this.takeoverTime = takeoverTime;
    }

    public String getOriginalTakeover() {
        return originalTakeover;
    }

    public void setOriginalTakeover(String originalTakeover) {
        this.originalTakeover = originalTakeover;
    }

    public FeedItem takeoverAsFeedItem() {
        try {
            // TODO should use singleton instead of creating every time
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(originalTakeover, FeedItem.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
