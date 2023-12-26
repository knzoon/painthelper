package org.knzoon.painthelper.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class BroadcastMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;

    private ZonedDateTime importNeededAfter;

    public BroadcastMessage() {
    }

    public BroadcastMessage(String message, ZonedDateTime importNeededAfter) {
        this.message = message;
        this.importNeededAfter = importNeededAfter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getImportNeededAfter() {
        return importNeededAfter;
    }

    public void setImportNeededAfter(ZonedDateTime importNeededAfter) {
        this.importNeededAfter = importNeededAfter;
    }
}
