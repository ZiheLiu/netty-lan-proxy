package com.ziheliu.common.protocol;

import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

public class ProxyEncoder extends MessageToMessageEncoder<ProxyMessage> {
  @Override
  protected void encode(ChannelHandlerContext ctx, ProxyMessage msg, List<Object> out)
      throws Exception {
    CompositeByteBuf buf = ctx.alloc().compositeBuffer(2);
    buf.addComponent(true,
        ctx.alloc().buffer(5)
          .writeByte(msg.getType())
          .writeInt(msg.getClientChannelId()));
    buf.addComponent(true, msg.getData());
    out.add(buf);
  }
}
