package com.ziheliu.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpEncoder extends MessageToByteEncoder<RpMessage> {
  @Override
  protected void encode(ChannelHandlerContext ctx, RpMessage msg, ByteBuf out) throws Exception {
    System.out.println("<RpEncoder.encode>");
    System.out.println("Type: " + msg.getType());
    System.out.println("Port: " + msg.getPort());

    out.writeByte(msg.getType());
    out.writeInt(msg.getPort());
    out.writeInt(msg.channelIdSize());
    out.writeBytes(msg.getChannelIdBytes());
    out.writeBytes(msg.getData());
  }
}
