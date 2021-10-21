package org.goldenglue.chat;

import io.netty.channel.ChannelId;
import org.goldenglue.network.UserContext;
import org.goldenglue.user.User;

import java.time.LocalTime;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class Room {
    private final String name;
    private final ConcurrentMap<ChannelId, UserContext> roomUsers = new ConcurrentHashMap<>(10);
    private final MessageStore messageStore;
    private final int roomCapacity;

    public Room(String name, int roomCapacity, MessageStore messageStore) {
        this.name = name;
        this.roomCapacity = roomCapacity;
        this.messageStore = messageStore;
    }

    public boolean tryEnter(ChannelId channelId, UserContext userContext) {
        synchronized (roomUsers) {
            if (roomUsers.size() == roomCapacity) {
                return false;
            }

            roomUsers.put(channelId, userContext);
        }

        messageStore.readStore(userContext::send);
        return true;
    }

    public void leave(ChannelId channelId) {
        roomUsers.remove(channelId);
    }

    public void send(User user, String message) {
        final Message msg = new Message(LocalTime.now(), user.getLogin(), message);
        messageStore.put(msg);
        roomUsers.forEach((channelId, userContext) -> userContext.send(msg));
    }

    public Collection<String> userList() {
        return roomUsers.values().stream()
                .map(UserContext::getLogin)
                .collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", roomUsers=" + roomUsers +
                ", messageStore=" + messageStore +
                ", roomCapacity=" + roomCapacity +
                '}';
    }
}
