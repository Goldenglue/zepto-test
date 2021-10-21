package org.goldenglue.chat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private final String text;
    private final LocalTime time;
    private final String login;

    public Message(LocalTime time, String login, String message) {
        this.text = String.format("%s %s: %s", formatter.format(time), login, message);
        this.time = time;
        this.login = login;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", time=" + time +
                ", login='" + login + '\'' +
                '}';
    }
}
