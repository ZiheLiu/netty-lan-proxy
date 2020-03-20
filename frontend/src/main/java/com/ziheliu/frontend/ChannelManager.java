package com.ziheliu.frontend;

import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelManager {
  private static
      Map<Integer, List<ChannelHandlerContext>> port2backendCtx = new ConcurrentHashMap<>();
  //  private static Map<Integer, AtomicInteger> port2counter = new ConcurrentHashMap<>();
  private static Map<ChannelHandlerContext, Integer> backendCtx2port = new ConcurrentHashMap<>();

  private static
      Map<Integer, ChannelHandlerContext> clientChannelId2ctx = new ConcurrentHashMap<>();

  private static AtomicInteger nextClientChannelId = new AtomicInteger();


  public static boolean putBackendCtx(int port, ChannelHandlerContext ctx) {
    port2backendCtx.computeIfAbsent(port, (key) -> new CopyOnWriteArrayList<>());

    List<ChannelHandlerContext> ctxList = port2backendCtx.get(port);
    boolean notFound = ctxList.indexOf(ctx) == -1;
    if (notFound) {
      ctxList.add(ctx);
      backendCtx2port.put(ctx, port);
    }
    return notFound;
  }

  public static ChannelHandlerContext getBackendCtx(int port) {
    List<ChannelHandlerContext> ctxList = port2backendCtx.get(port);
    return ctxList.get(ThreadLocalRandom.current().nextInt(0, ctxList.size()));
  }

  public static List<ChannelHandlerContext> getBackendCtxList(int port) {
    return port2backendCtx.get(port);
  }

  public static Integer removeBackendCtx(ChannelHandlerContext ctx) {
    Integer port = backendCtx2port.remove(ctx);
    if (port == null) {
      return null;
    }

    List<ChannelHandlerContext> ctxList = port2backendCtx.get(port);
    ctxList.remove(ctx);
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
