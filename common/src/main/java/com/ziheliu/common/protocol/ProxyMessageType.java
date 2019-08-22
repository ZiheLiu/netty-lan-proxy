package com.ziheliu.common.protocol;

public interface ProxyMessageType {
  public static final byte BACKEND_CONNECT = 0;
  public static final byte CLIENT_DISCONNECT = 1;
  public static final byte SERVER_DATA = 2;
  public static final byte CLIENT_DATA = 3;
  public static final byte BACKEND_HEART_BEAT = 4;
}
