package com.ziheliu.frontend;

import com.ziheliu.common.protocol.ProxyMessage;
import com.ziheliu.common.protocol.ProxyMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends ChannelInboundHandlerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
    int frontendPort = address.getPort();
    ctx.channel().attr(Constants.FRONTEND_PORT).set(frontendPort);

    int channelId = ChannelManager.putClientCtx(ctx);
    ctx.channel().attr(Constants.CHANNEL_ID).set(channelId);

    super.channelActive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;

    Channel channel = ctx.channel();

    int channelId = channel.attr(Constants.CHANNEL_ID).get();
    ProxyMessage proxyMessage = new ProxyMessage(ProxyMessageType.CLIENT_DATA, channelId, buf);

    int frontendPort = ctx.channel().attr(Constants.FRONTEND_PORT).get();
    ChannelHandlerContext backendCtx = ChannelManager.getBackendCtx(frontendPort);

    backendCtx.write(proxyMessage);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    int frontendPort = ctx.channel().attr(Constants.FRONTEND_PORT).get();
    for (ChannelHandlerContext backendCtx : ChannelManager.getBackendCtxList(frontendPort)) {
      backendCtx.flush();
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOGGER.error("Client Channel gets error: " + cause);
  }
}
