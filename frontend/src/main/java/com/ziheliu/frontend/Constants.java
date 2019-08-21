package com.ziheliu.frontend;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public interface Constants {
  public static final
  AttributeKey<ChannelHandlerContext> BACKEND_CTX = AttributeKey.newInstance("BACKEND_CTX");

  public static final
  AttributeKey<Integer> CHANNEL_ID = AttributeKey.newInstance("CHANNEL_ID");

}
