package com.ziheliu;

import com.ziheliu.protocol.RpMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SubChannelHandler extends ChannelInboundHandlerAdapter {
  private final ChannelHandlerContext ctx;
  private final RpMessage rpMessage;

  public SubChannelHandler(ChannelHandlerContext ctx, RpMessage rpMessage) {
    this.ctx = ctx;
    this.rpMessage = rpMessage;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx2) throws Exception {
    System.out.println("<<SubChannelHandler.channelActive>>");
    ctx2.writeAndFlush(rpMessage.getData());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx2) throws Exception {
    System.out.println("<<SubChannelHandler.channelInactive>>");
    RpMessage res = new RpMessage((byte) 1,
        rpMessage.getPort(),
        rpMessage.getChannelId(),
        Unpooled.EMPTY_BUFFER);
    ctx.writeAndFlush(res);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx2, Object msg) throws Exception {
    System.out.println("<<SubChannelHandler.channelRead>>");
    ByteBuf buf = (ByteBuf) msg;

    RpMessage res = new RpMessage((byte) 0,
        rpMessage.getPort(),
        rpMessage.getChannelId(),
        buf);
    ctx.write(res);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx2) throws Exception {
    System.out.println("<<SubChannelHandler.channelReadComplete>>");
    ctx.flush();
  }
}
