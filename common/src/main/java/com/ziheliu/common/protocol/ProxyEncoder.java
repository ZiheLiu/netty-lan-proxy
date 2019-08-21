package com.ziheliu.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProxyEncoder extends MessageToByteEncoder<ProxyMessage> {
  @Override
  protected void encode(ChannelHandlerContext ctx, ProxyMessage msg, ByteBuf out) throws Exception {
    out.writeByte(msg.getType());
    out.writeInt(msg.getClientChannelId());
    out.writeBytes(msg.getData());
  }
}
