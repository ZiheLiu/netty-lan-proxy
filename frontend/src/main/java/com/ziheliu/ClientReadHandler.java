package com.ziheliu;

import com.ziheliu.protocol.RpMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;

public class ClientReadHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("<ClientReadHandler channelRead>");

    if (!FrontendCreateChannelHandler.connected2backend()) {
      ctx.close();
    } else {
      ByteBuf buf = (ByteBuf) msg;

      Channel channel = ctx.channel();

      InetSocketAddress address = (InetSocketAddress) channel.localAddress();
      int frontendPort = address.getPort();
      int backendPort = Config.getInstance().getBackendPort(frontendPort);

      RpMessage rpMessage = new RpMessage((byte) 0, backendPort, channel, buf);
      FrontendCreateChannelHandler.send(rpMessage);
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    System.out.println("<ClientReadHandler channelReadComplete>");
    //    FrontendCreateChannelHandler.flush();
  }
}
