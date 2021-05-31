package com.appsdeveloperblog.app.ws.ui.model.response;

import java.util.Date;

// POJO
public class ErrorMessage {
    private Date timestamp;
    private String message;


    // empty constructor
    public ErrorMessage() {
    }

    // constructor, taking 2 arguments
    public ErrorMessage(Date timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}