package com.ziheliu.backend;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {
  private static
      Map<Integer, Channel> channelId2serverChannel = new ConcurrentHashMap<>();


  public static Channel getServerChannel(int channelId) {
    return channelId2serverChannel.get(channelId);
  }

  public static void putServerChannel(int channelId, Channel channel) {
    channelId2serverChannel.put(channelId, channel);
  }

  public static void removeServerChannel(int channelId) {
    channelId2serverChannel.remove(channelId);
  }
}
