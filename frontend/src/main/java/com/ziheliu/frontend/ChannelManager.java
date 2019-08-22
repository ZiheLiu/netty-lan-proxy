package com.ziheliu.frontend;

import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelManager {
  private static Map<Integer, ChannelHandlerContext> port2backendCtx = new ConcurrentHashMap<>();
  private static Map<ChannelHandlerContext, Integer> backendCtx2port = new ConcurrentHashMap<>();

  private static
      Map<Integer, ChannelHandlerContext> clientChannelId2ctx = new ConcurrentHashMap<>();

  private static AtomicInteger nextClientChannelId = new AtomicInteger();


  public static boolean putBackendCtx(int port, ChannelHandlerContext ctx) {
    ChannelHandlerContext prevCtx = port2backendCtx.putIfAbsent(port, ctx);
    boolean res = prevCtx == null;
    if (res) {
      backendCtx2port.put(ctx, port);
    }
    return res;
  }

  public static ChannelHandlerContext getBackendCtx(int port) {
    return port2backendCtx.get(port);
  }

  public static Integer removeBackendCtx(ChannelHandlerContext ctx) {
    Integer port = backendCtx2port.remove(ctx);
    if (port == null) {
      return null;
    }

    port2backendCtx.remove(port);
    return port;
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
