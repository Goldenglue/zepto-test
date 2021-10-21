package org.goldenglue.user;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UsersStorage {
    private final ConcurrentMap<String, User> usersByLogin = new ConcurrentHashMap<>();

    public User checkCredentials(String login, String password) {
        final User user = usersByLogin.computeIfAbsent(login, v -> new User(login, password));
        if (user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "UsersStorage{" +
                "usersByLogin=" + usersByLogin +
                '}';
    }
}
