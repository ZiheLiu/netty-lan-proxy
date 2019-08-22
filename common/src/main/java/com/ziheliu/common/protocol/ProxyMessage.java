package com.ziheliu.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

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
    assert type == ProxyMessageType.BACKEND_CONNECT;
    return clientChannelId;
  }

  public String getPassword() {
    assert type == ProxyMessageType.BACKEND_CONNECT;
    return data.toString(CharsetUtil.UTF_8);
  }

  public ByteBuf getData() {
    return data;
  }
}
