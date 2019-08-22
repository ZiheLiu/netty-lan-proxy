package com.ziheliu.frontend;

import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelManager {
  private static Map<Integer, ChannelHandlerContext> port2backendCtx = new ConcurrentHashMap<>();

  private static
      Map<Integer, ChannelHandlerContext> clientChannelId2ctx = new ConcurrentHashMap<>();

  private static AtomicInteger nextClientChannelId = new AtomicInteger();


  public static void putBackendCtx(int port, ChannelHandlerContext ctx) {
    port2backendCtx.put(port, ctx);
  }

  public static ChannelHandlerContext getBackendCtx(int port) {
    return port2backendCtx.get(port);
  }

  public static int putClientCtx(ChannelHandlerContext ctx) {
    int channelId = nextClientChannelId.getAndIncrement();
    clientChannelId2ctx.put(channelId, ctx);
    return channelId;
  }

  public static ChannelHandlerContext getClientCtx(int channelId) {
    return clientChannelId2ctx.get(channelId);
  }

  public static void removeClientCtx(int channelId) {
    clientChannelId2ctx.remove(channelId);
  }
}
