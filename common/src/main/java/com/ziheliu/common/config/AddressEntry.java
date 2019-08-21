package com.ziheliu.common.config;

public class AddressEntry {
  private Address frontendAddr;
  private Address serverAddr;

  public Address getFrontendAddr() {
    return frontendAddr;
  }

  public void setFrontendAddr(Address frontendAddr) {
    this.frontendAddr = frontendAddr;
  }

  public Address getServerAddr() {
    return serverAddr;
  }

  public void setServerAddr(Address serverAddr) {
    this.serverAddr = serverAddr;
  }

  @Override
  public String toString() {
    return frontendAddr + "@" + serverAddr;
  }
}
