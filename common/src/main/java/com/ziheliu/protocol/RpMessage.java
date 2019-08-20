package com.ziheliu.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpMessage {
  private static final Map<String, Channel> id2channel = new ConcurrentHashMap<>();

  // 0: 正常消息
  // 1: 关闭信号
  private final byte type;
  private final int port;
  private final String channelId;
  private final ByteBuf data;

  private byte[] channelIdBytes;

  public RpMessage(byte type, int port, Channel channel, ByteBuf data) {
    this.type = type;
    this.port = port;
    this.channelId = channel.id().asLongText();
    this.data = data;

    id2channel.put(channelId, channel);
  }

  public RpMessage(byte type, int port, String channelId, ByteBuf data) {
    this.type = type;
    this.port = port;
    this.channelId = channelId;
    this.data = data;
  }

  public int channelIdSize() {
    return channelId.length();
  }

  public byte getType() {
    return type;
  }

  public int getPort() {
    return port;
  }

  public String getChannelId() {
    return channelId;
  }

  public byte[] getChannelIdBytes() {
    if (channelIdBytes == null) {
      channelIdBytes = channelId.getBytes(CharsetUtil.UTF_8);
    }
    return channelIdBytes;
  }

  public Channel getChannel() {
    return id2channel.get(channelId);
  }

  public ByteBuf getData() {
    return data;
  }
}
