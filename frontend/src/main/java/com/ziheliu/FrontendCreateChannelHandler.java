package com.ziheliu;

import com.ziheliu.protocol.RpDecoder;
import com.ziheliu.protocol.RpEncoder;
import com.ziheliu.protocol.RpMessage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class FrontendCreateChannelHandler extends ChannelInboundHandlerAdapter {
  private static ChannelHandlerContext backendCtx;

  public static void send(RpMessage msg) {
    System.out.println("<FrontendCreateChannelHandler send>: " + msg.getPort());
    backendCtx.write(msg);
  }

  public static void flush() {
    backendCtx.flush();
  }

  public static boolean connected2backend() {
    return backendCtx != null;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if (backendCtx != null) {
      if (!backendCtx.equals(ctx)) {
        ctx
          .writeAndFlush("Frontend already connected to a backend.")
          .addListener(ChannelFutureListener.CLOSE);
      }
    } else {
      System.out.println("Create Channel");
      backendCtx = ctx;
      ctx.pipeline()
        .addFirst(new RpEncoder())
        .addFirst(new LengthFieldPrepender(4))

        .addLast(new LengthFieldBasedFrameDecoder(60 * 1024, 0, 4, 0, 4))
        .addLast(new RpDecoder())
        .addLast(new BackendReadHandler());
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    ctx.close();
    if (backendCtx.equals(ctx)) {
      System.out.println("Close Channel");
      backendCtx = null;
    }
  }
}
