package com.ziheliu;

import com.ziheliu.protocol.RpMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;

public class FrontendReadHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("<FrontendReadHandler.channelActive>");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("<FrontendReadHandler.channelRead>");

    RpMessage rpMessage = (RpMessage) msg;
    rpMessage.getData().retain();
    send2server(ctx, rpMessage);
  }

  private void send2server(ChannelHandlerContext ctx, RpMessage rpMessage) {
    System.out.println("<FrontendReadHandler.send2server> " + rpMessage.getPort());

    Bootstrap bootstrap = new Bootstrap();
    bootstrap
      .group(new NioEventLoopGroup())
      .channel(NioSocketChannel.class)
      .handler(new SubChannelHandler(ctx, rpMessage));

    bootstrap.connect(new InetSocketAddress("localhost", rpMessage.getPort()));
  }
}
