package org.goldenglue.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import org.goldenglue.chat.Room;
import org.goldenglue.chat.RoomRegistry;
import org.goldenglue.user.User;
import org.goldenglue.user.UsersStorage;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class RequestHandler extends ChannelDuplexHandler {
    private final UsersStorage usersStorage;
    private final RoomRegistry roomRegistry;
    private UserContext userContext;
    private Room room;
    private ChannelHandlerContext ctx;

    public RequestHandler(UsersStorage usersStorage, RoomRegistry roomRegistry) {
        this.usersStorage = usersStorage;
        this.roomRegistry = roomRegistry;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        room.leave(ctx.channel().id());
        userContext = null;
        room = null;
        this.ctx = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            String message = ((ByteBuf) msg).toString(StandardCharsets.UTF_8);
            if (isCommand(message)) {
                executeCommand(message);
            } else {
                if (userContext != null && room != null) {
                    room.send(userContext.getUser(), message);
                } else {
                    respond("Not logged in or not joined");
                }
            }
        }
    }

    private boolean isCommand(String message) {
        return message.startsWith("/");
    }

    private void executeCommand(String message) {
        if (message.startsWith("/login")) {
            final String[] args = message.split("\\s");
            if (args.length != 3) {
                respond("Not enough or too many arguments for login command, usage: /login <name> <password>");
                return;
            }

            if (userContext != null) {
                respond("Already logged in");
                return;
            }

            final User user = usersStorage.checkCredentials(args[1], args[2]);
            if (user != null) {
                userContext = new UserContext(user, this);
                respond("Logged in");
            } else {
                respond("Wrong credentials");
            }
        } else if (message.startsWith("/join")) {
            final String[] args = message.split("\\s");
            if (args.length != 2) {
                respond("Not enough or too many arguments for join command, usage: /join <channel>");
                return;
            }

            if (this.room != null) {
                respond("Already joined");
                return;
            }

            final Room room = roomRegistry.join(ctx.channel().id(), userContext, args[1]);
            if (room != null) {
                this.room = room;
                respond("Joined");
            } else {
                respond("Room is full");
            }
        } else if (message.startsWith("/users")) {
            final Collection<String> users = room.userList();
            final StringBuilder buf = new StringBuilder();
            buf.append(room.getName()).append(" users:").append('\n');
            for (String user : users) {
                buf.append(user).append("\n");
            }

            respond(buf.toString());
        } else if (message.startsWith("/leave")) {
            final ChannelFuture future = respond("Closing");
            if (future == null) {
                throw new IllegalStateException("Attempting to close non-active channel");
            }

            future.addListener(f -> room.leave(ctx.channel().id()))
                    .addListener(ChannelFutureListener.CLOSE);
        } else {
            respond("Unknown command, commands: " +
                    "\n/leave <name> <password> -  if user not exists create profile else login" +
                    "\n/join <channel> - try to join channel (max 10 active clients per channel is needed). If client's limit exceeded - sends error, otherwise joins channel and sends last N messages of activity" +
                    "\n/leave - disconnect client" +
                    "\n/users - show users in the channel");
        }
    }

    ChannelFuture respond(String response) {
        if (ctx != null && ctx.channel().isActive()) {
            return ctx.channel().writeAndFlush(ByteBufUtil.writeUtf8(ctx.alloc(), response));
        }

        return null;
    }
}
