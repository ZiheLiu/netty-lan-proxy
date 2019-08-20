package com.ziheliu;

import com.ziheliu.protocol.RpDecoder;
import com.ziheliu.protocol.RpEncoder;
import com.ziheliu.protocol.RpMessage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import java.util.concurrent.atomic.AtomicReference;

public class FrontendCreateChannelHandler extends ChannelInboundHandlerAdapter {
  private static AtomicReference<ChannelHandlerContext> backendCtxRef = new AtomicReference<>(null);

  public static void send(RpMessage msg) {
    System.out.println("<FrontendCreateChannelHandler send>: " + msg.getPort());
    backendCtxRef.get().write(msg);
  }

  public static void flush() {
    backendCtxRef.get().flush();
  }

  public static boolean connected2backend() {
    return backendCtxRef.get() != null;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ChannelHandlerContext backendCtx = backendCtxRef.get();
    if (backendCtx!= null) {
      if (!backendCtx.equals(ctx)) {
        ctx
          .writeAndFlush("Frontend already connected to a backend.")
          .addListener(ChannelFutureListener.CLOSE);
      }
    } else {
      System.out.println("Create Channel");
      if (backendCtxRef.compareAndSet(null, ctx)) {
        ctx.pipeline()
          .addFirst(new RpEncoder())
          .addFirst(new LengthFieldPrepender(4))

          .addLast(new LengthFieldBasedFrameDecoder(60 * 1024, 0, 4, 0, 4))
          .addLast(new RpDecoder())
          .addLast(new BackendReadHandler());
      }
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    ctx.close();
    if (backendCtxRef.get().equals(ctx)) {
      System.out.println("Close Channel");
      backendCtxRef.set(null);
    }
  }
}
