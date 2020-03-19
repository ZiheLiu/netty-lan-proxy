package com.ziheliu.common.utils;

public final class OsUtil {
  public enum OsType {
    MaxOS, Linux, Other
  }

  public static final OsType osType;

  static {
    String os = System.getProperty("os.name");
    if (os == null) {
      osType = OsType.Other;
    } else {
      os = os.toLowerCase();
      if (os.contains("linux")) {
        osType = OsType.Linux;
      } else if (os.contains("mac")) {
        osType = OsType.MaxOS;
      } else {
        osType = OsType.Other;
      }
    }
  }
}
