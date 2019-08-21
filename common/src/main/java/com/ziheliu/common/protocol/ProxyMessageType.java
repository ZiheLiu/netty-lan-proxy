package com.ziheliu.common.protocol;

public interface ProxyMessageType {
  public static final byte SETUP_BACKEND_CONNECTION = 0;
  public static final byte CLOSE_CLIENT_CONNECTION = 1;
  public static final byte RESPONSE_DATA = 2;
  public static final byte REQUEST_DATA = 3;
}
