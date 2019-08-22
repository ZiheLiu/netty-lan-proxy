package com.ziheliu;

import com.ziheliu.common.protocol.ProxyMessage;
import com.ziheliu.common.protocol.ProxyMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends ChannelInboundHandlerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

  private final ChannelHandlerContext ctx;
  private final ProxyMessage proxyMessage;

  public ServerHandler(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
    this.ctx = ctx;
    this.proxyMessage = proxyMessage;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx2) throws Exception {
    ctx2.writeAndFlush(proxyMessage.getData());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx2) throws Exception {
    ChannelManager.removeServerChannel(proxyMessage.getClientChannelId());

    ProxyMessage res = new ProxyMessage(ProxyMessageType.CLOSE_CLIENT_CONNECTION,
        proxyMessage.getClientChannelId(),
        Unpooled.EMPTY_BUFFER);
    ctx.writeAndFlush(res);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx2, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;

    ProxyMessage res = new ProxyMessage(ProxyMessageType.RESPONSE_DATA,
        proxyMessage.getClientChannelId(),
        buf);
    ctx.write(res);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx2) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx2, Throwable cause) throws Exception {
    LOGGER.error(cause.getMessage());

    ProxyMessage res = new ProxyMessage(ProxyMessageType.CLOSE_CLIENT_CONNECTION,
    proxyMessage.getClientChannelId(),
    Unpooled.EMPTY_BUFFER);
    ctx.writeAndFlush(res);

    ctx2.close();
  }
}
