package org.goldenglue.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class LineEncoder extends MessageToByteEncoder<ByteBuf> {
    public LineEncoder() {
        // NOOP
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        out.writeBytes(msg);
        out.writeByte('\r');
        out.writeByte('\n');
    }
}
