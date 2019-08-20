package com.ziheliu.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;

public class RpDecoder extends ByteToMessageDecoder {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    System.out.println("<RpDecoder.decode> " + in.readableBytes());

    final byte type = in.readByte();
    final int port = in.readInt();
    final int channelIdSize = in.readInt();
    String channelId = in.readBytes(channelIdSize).toString(CharsetUtil.UTF_8);
    ByteBuf data = in.slice().retain();
    in.readerIndex(in.writerIndex());

    RpMessage msg = new RpMessage(type, port, channelId, data);

    out.add(msg);
  }
}
