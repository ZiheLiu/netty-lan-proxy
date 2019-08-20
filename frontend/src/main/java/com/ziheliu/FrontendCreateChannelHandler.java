package com.ziheliu;

import com.ziheliu.protocol.RpDecoder;
import com.ziheliu.protocol.RpEncoder;
import com.ziheliu.protocol.RpMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class FrontendCreateChannelHandler extends ChannelInboundHandlerAdapter {
  private static Channel backendChannel;

  public static void send(RpMessage msg) {
    System.out.println("<FrontendCreateChannelHandler send>: " + msg.getPort());
    backendChannel.writeAndFlush(msg);
  }

  public static void flush() {
  }

  public static boolean connected2backend() {
    return backendChannel != null;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if (backendChannel != null) {
      if (!backendChannel.equals(ctx.channel())) {
        ctx
          .writeAndFlush("Frontend already connected to a backend.")
          .addListener(ChannelFutureListener.CLOSE);
      }
    } else {
      System.out.println("Create Channel");
      backendChannel = ctx.channel();
      ctx.pipeline()
        .addLast(new LengthFieldBasedFrameDecoder(60 * 1024, 0, 4, 0, 4))
        .addLast(new RpDecoder())
        .addLast(new BackendReadHandler())

        .addLast(new LengthFieldPrepender(4))
        .addLast(new RpEncoder());
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    ctx.close();
    if (backendChannel.equals(ctx.channel())) {
      System.out.println("Close Channel");
      backendChannel = null;
    }
  }
}
