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
    ChannelHandlerContext backendCtx = ChannelManager.getBackendCtx(frontendPort);

    if (backendCtx == null) {
      LOGGER.error("The port {} do not create channel to backend.", frontendPort);
      ctx.close();
    } else {
      ctx.channel().attr(Constants.BACKEND_CTX).set(backendCtx);

      int channelId = ChannelManager.putClientCtx(ctx);
      ctx.channel().attr(Constants.CHANNEL_ID).set(channelId);
    }

    super.channelActive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;

    Channel channel = ctx.channel();

    int channelId = channel.attr(Constants.CHANNEL_ID).get();
    ProxyMessage proxyMessage = new ProxyMessage(ProxyMessageType.REQUEST_DATA, channelId, buf);

    ChannelHandlerContext backendCtx = channel.attr(Constants.BACKEND_CTX).get();
    backendCtx.write(proxyMessage);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();

    ChannelHandlerContext backendCtx = channel.attr(Constants.BACKEND_CTX).get();
    backendCtx.flush();
  }
}
