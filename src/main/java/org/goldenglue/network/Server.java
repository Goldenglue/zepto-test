package org.goldenglue.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.goldenglue.chat.RoomRegistry;
import org.goldenglue.user.UsersStorage;

public class Server {
    private final int port;
    private final NioEventLoopGroup eventLoopGroup;
    private final RoomRegistry roomRegistry;
    private final UsersStorage usersStorage;

    public Server(int port, int roomCapacity, int historyCapacity) {
        this.port = port;
        this.eventLoopGroup = new NioEventLoopGroup();
        roomRegistry = new RoomRegistry(roomCapacity, historyCapacity);
        usersStorage = new UsersStorage();
    }

    public void start() {
        try {
            new ServerBootstrap()
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatChannelInitializer(2048, usersStorage, roomRegistry))
                    .bind(port)
                    .sync();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while binding to port", e);
        }
    }

}
