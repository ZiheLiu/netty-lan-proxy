package com.ziheliu.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ProxyDecoder extends LengthFieldBasedFrameDecoder {
  public ProxyDecoder() {
    super((1 << 16) - 1, 0, 2, 0, 2);
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    ByteBuf frame = (ByteBuf) super.decode(ctx, in);
    if (frame == null) {
      return null;
    }

    final byte type = frame.readByte();
    final int clientChannelId = frame.readInt();

    ByteBuf data = frame.slice();

    return new ProxyMessage(type, clientChannelId, data);
  }
}
