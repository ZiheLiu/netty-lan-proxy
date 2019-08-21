package com.ziheliu.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class ProxyDecoder extends ByteToMessageDecoder {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    final byte type = in.readByte();
    final int clientChannelId = in.readInt();

    ByteBuf data = in.slice().retain();
    in.readerIndex(in.writerIndex());

    ProxyMessage msg = new ProxyMessage(type, clientChannelId, data);

    out.add(msg);
  }
}
