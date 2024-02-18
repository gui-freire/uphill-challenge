package com.uphill.codechallenge.enums;

public enum MessagesEnum {
    GREETING("HI, I AM"),
    GOODBYE("BYE MATE!"),
    ADD_NODE("ADD NODE"),
    REMOVE_NODE("REMOVE NODE"),
    ADD_EDGE("ADD EDGE"),
    REMOVE_EDGE("REMOVE EDGE"),
    CLOSER_THAN("CLOSER THAN"),
    SHORTEST_PATH("SHORTEST PATH"),
    UNKNOWN("");

    private final String message;

    MessagesEnum(String message) {
        this.message = message;
    }

    public static MessagesEnum findValue(String message) {
        for (MessagesEnum messagesEnum: MessagesEnum.values()) {
            if (message.contains(messagesEnum.getMessage())) {
                return messagesEnum;
            }
        }
        return MessagesEnum.UNKNOWN;
    }

    public String getMessage() {
        return message;
    }
}
