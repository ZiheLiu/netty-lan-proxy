package com.ziheliu.frontend;

import com.ziheliu.common.protocol.ProxyMessage;
import com.ziheliu.common.protocol.ProxyMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendHandler extends ChannelInboundHandlerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(BackendHandler.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ProxyMessage proxyMessage = (ProxyMessage) msg;
    switch (proxyMessage.getType()) {
      case ProxyMessageType.SETUP_BACKEND_CONNECTION:
        handleSetupBackendConnection(ctx, proxyMessage);
        break;
      case ProxyMessageType.CLOSE_CLIENT_CONNECTION:
        handleCloseClientConnection(proxyMessage);
        break;
      case ProxyMessageType.RESPONSE_DATA:
        handleResponseData(proxyMessage);
        break;
      default:
        LOGGER.error("ProxyMessage.type is invalid: " + proxyMessage.getType());
        System.exit(-1);
    }

    super.channelRead(ctx, msg);
  }

  private void handleSetupBackendConnection(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
    LOGGER.info("Create channel to backend to watch frontend port: {}",
        proxyMessage.getClientPort());

    ChannelManager.putBackendCtx(proxyMessage.getClientPort(), ctx);
    proxyMessage.getData().release();
  }

  private void handleCloseClientConnection(ProxyMessage proxyMessage) {
    LOGGER.info("Close client connection with channelId#{}", proxyMessage.getClientChannelId());

    ChannelManager.getClientCtx(proxyMessage.getClientChannelId()).close();
    proxyMessage.getData().release();
  }

  private void handleResponseData(ProxyMessage proxyMessage) {
    ChannelManager.getClientCtx(proxyMessage.getClientChannelId())
        .writeAndFlush(proxyMessage.getData());
  }
}
