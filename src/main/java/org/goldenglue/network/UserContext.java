package org.goldenglue.network;

import org.goldenglue.chat.Message;
import org.goldenglue.user.User;

public class UserContext {
    private final User user;
    private final RequestHandler handler;

    public UserContext(User user, RequestHandler handler) {
        this.user = user;
        this.handler = handler;
    }

    public String getLogin() {
        return user.getLogin();
    }

    public void send(Message message) {
        handler.respond(message.getText());
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UserContext{" +
                "user=" + user +
                ", channel=" + handler +
                '}';
    }
}
