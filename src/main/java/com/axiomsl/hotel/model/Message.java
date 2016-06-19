package com.axiomsl.hotel.model;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Message class with type
 */
public class Message {
    private String type;
    private String message;

    public Message(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
