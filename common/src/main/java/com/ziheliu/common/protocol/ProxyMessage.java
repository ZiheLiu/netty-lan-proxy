package com.ziheliu.common.protocol;

import io.netty.buffer.ByteBuf;

public class ProxyMessage {

  private final byte type;
  private final int clientChannelId;
  private final ByteBuf data;

  public ProxyMessage(byte type, int clientChannelId, ByteBuf data) {
    this.type = type;
    this.clientChannelId = clientChannelId;
    this.data = data;
  }

  public byte getType() {
    return type;
  }

  public int getClientChannelId() {
    return clientChannelId;
  }

  public int getClientPort() {
    if (type == ProxyMessageType.SETUP_BACKEND_CONNECTION) {
      return clientChannelId;
    }
    return -1;
  }

  public ByteBuf getData() {
    return data;
  }
}
