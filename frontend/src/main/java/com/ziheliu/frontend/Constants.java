package com.ziheliu.frontend;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public interface Constants {
  public static final
      AttributeKey<Integer> FRONTEND_PORT = AttributeKey.newInstance("FRONTEND_PORT");

  public static final
      AttributeKey<Integer> CHANNEL_ID = AttributeKey.newInstance("CHANNEL_ID");

}
