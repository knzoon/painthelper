package org.knzoon.painthelper.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
