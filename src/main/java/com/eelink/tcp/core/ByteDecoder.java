package com.eelink.tcp.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteDecoder extends ByteToMessageDecoder {
    protected void decode( ChannelHandlerContext ctx, ByteBuf in, List<Object> out ) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes( bytes );
        out.add( bytes );
    }
}
