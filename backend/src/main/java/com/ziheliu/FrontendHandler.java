package com.ziheliu;

import com.ziheliu.common.config.Address;
import com.ziheliu.common.config.AddressEntry;
import com.ziheliu.common.protocol.ProxyMessage;
import com.ziheliu.common.protocol.ProxyMessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontendHandler extends ChannelInboundHandlerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(FrontendHandler.class);

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    AddressEntry entry = ctx.channel().attr(Constants.ADDRESS_ENTRY).get();

    LOGGER.info("Create channel to frontend ({}:{})",
        entry.getFrontendAddr().getHost(),
        entry.getFrontendAddr().getPort());

    ProxyMessage msg = new ProxyMessage(ProxyMessageType.SETUP_BACKEND_CONNECTION,
        entry.getFrontendAddr().getPort(), Unpooled.EMPTY_BUFFER);
    ctx.writeAndFlush(msg);

    super.channelActive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ProxyMessage proxyMessage = (ProxyMessage) msg;
    send2server(ctx, proxyMessage);
  }

  private void send2server(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
    Bootstrap bootstrap = new Bootstrap();
    bootstrap
      .group(ctx.channel().eventLoop())
      .channel(NioSocketChannel.class)
      .handler(new ServerHandler(ctx, proxyMessage));

    AddressEntry entry = ctx.channel().attr(Constants.ADDRESS_ENTRY).get();
    Address serverAddr = entry.getServerAddr();
    InetSocketAddress socketAddress = new InetSocketAddress(
        serverAddr.getHost(), serverAddr.getPort());

    ChannelFuture future = bootstrap.connect(socketAddress);
    future.addListener(f -> {
      if (!f.isSuccess()) {
        LOGGER.error("Connect to {}:{} failed, cause: {}.",
            serverAddr.getHost(), serverAddr.getPort(), f.cause());
        proxyMessage.getData().release();
        ctx.writeAndFlush(new ProxyMessage(ProxyMessageType.CLOSE_CLIENT_CONNECTION,
            proxyMessage.getClientChannelId(),
            Unpooled.EMPTY_BUFFER));
      }
    });
  }
}
