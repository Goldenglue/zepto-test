package org.goldenglue.chat;

import io.netty.channel.ChannelId;
import org.goldenglue.network.UserContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RoomRegistry {
    private final ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();
    private final int roomCapacity;
    private final int historyCapacity;

    public RoomRegistry(int roomCapacity, int historyCapacity) {
        this.roomCapacity = roomCapacity;
        this.historyCapacity = historyCapacity;
    }

    public Room join(ChannelId channelId, UserContext userContext, String name) {
        final Room room = rooms.computeIfAbsent(name, v -> new Room(name, roomCapacity, new MessageStore(historyCapacity)));
        final boolean didEnter = room.tryEnter(channelId, userContext);
        return didEnter ? room : null;
    }
}
