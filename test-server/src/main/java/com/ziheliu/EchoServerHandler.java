package com.ziheliu;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("<EchoServerHandler.channelActive>");
    super.channelActive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf in = (ByteBuf) msg;
    System.out.println(
        "<EchoServerHandler.channelRead>, received: " + in.toString(CharsetUtil.UTF_8));

    CompositeByteBuf buf = ByteBufAllocator.DEFAULT.compositeBuffer();
    ByteBuf headerBuf = Unpooled.copiedBuffer("SERVER: ", CharsetUtil.UTF_8);
    buf.addComponents(headerBuf, in);
    buf.writerIndex(headerBuf.writerIndex() + in.writerIndex());

    ctx.write(buf);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }

}
