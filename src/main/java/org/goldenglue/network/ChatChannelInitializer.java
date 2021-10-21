package org.goldenglue.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import org.goldenglue.chat.RoomRegistry;
import org.goldenglue.user.UsersStorage;

public class ChatChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final LineEncoder lineEncoder = new LineEncoder();
    private final int maxFrameLength;
    private final UsersStorage usersStorage;
    private final RoomRegistry roomRegistry;

    public ChatChannelInitializer(int maxFrameLength, UsersStorage usersStorage, RoomRegistry roomRegistry) {
        this.maxFrameLength = maxFrameLength;
        this.usersStorage = usersStorage;
        this.roomRegistry = roomRegistry;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline p = ch.pipeline();

        p.addLast(new DelimiterBasedFrameDecoder(maxFrameLength, Delimiters.lineDelimiter()));
        p.addLast(lineEncoder);
        p.addLast(new RequestHandler(usersStorage, roomRegistry));
    }
}
