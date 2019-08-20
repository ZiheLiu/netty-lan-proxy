package com.ziheliu;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public final class Config {
  private static final Config instance;

  static {
    Map<Integer, InetSocketAddress> addressMap = new HashMap<>();
    addressMap.put(8001, new InetSocketAddress("localhost", 9001));
    instance = new Config(8000, addressMap);
  }

  public static Config getInstance() {
    return instance;
  }

  private final int port;
  private final Map<Integer, InetSocketAddress> addressMap;

  public Config(int port, Map<Integer, InetSocketAddress> addressMap) {
    this.port = port;
    this.addressMap = addressMap;
  }

  public int getPort() {
    return port;
  }

  public Map<Integer, InetSocketAddress> getAddressMap() {
    return addressMap;
  }

  public int getBackendPort(int frontendPort) {
    return addressMap.get(frontendPort).getPort();
  }
}
