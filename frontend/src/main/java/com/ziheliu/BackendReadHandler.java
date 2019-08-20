package com.ziheliu;

import com.ziheliu.protocol.RpMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BackendReadHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("<BackendReadHandler.channelRead>");
    RpMessage rpMessage = (RpMessage) msg;

    if (rpMessage.getType() == 1) {
      rpMessage.getChannel().close();
    } else {
      rpMessage.getChannel().writeAndFlush(rpMessage.getData());
    }

  }
}
