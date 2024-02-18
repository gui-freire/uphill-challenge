package com.uphill.codechallenge.model;

public class MessageResponse {

    private String message;
    private String clientId;

    public MessageResponse(String clientId) {
        this.clientId = clientId;
        this.message = "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
