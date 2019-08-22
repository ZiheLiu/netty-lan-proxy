package com.ziheliu.common;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SslContextFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(SslContextFactory.class);

  private static final String SERVER_KEY_STORE_PATH = "ssl/server.jks";
  private static final String SERVER_CA_PATH = "ssl/server-trust.jks";
  private static final String CLIENT_KEY_STORE_PATH = "ssl/client.jks";
  private static final String CLIENT_CA_PATH = "ssl/client-trust.jks";
  private static final String SERVER_PASSWORD = "sNetty";
  private static final String CLIENT_PASSWORD = "cNetty";

  private static final String PROTOCOL = "TLS";

  // 服务器安全套接字协议
  private static final SSLContext SERVER_CONTEXT;

  // 客户端安全套接字协议
  private static final SSLContext CLIENT_CONTEXT;

  static {
    SERVER_CONTEXT = initContext(SERVER_KEY_STORE_PATH, SERVER_CA_PATH, SERVER_PASSWORD);
    CLIENT_CONTEXT = initContext(CLIENT_KEY_STORE_PATH, CLIENT_CA_PATH, CLIENT_PASSWORD);
  }

  public static SSLContext getServerContext() {
    return SERVER_CONTEXT;
  }

  public static SSLContext getClientContext() {
    return CLIENT_CONTEXT;
  }

  private static SSLContext initContext(String ksPath, String caPath, String password) {
    InputStream ksIn = null;
    InputStream caIn = null;
    SSLContext context = null;
    try {
      // 密钥管理器
      KeyStore ks = KeyStore.getInstance("jks");
      ksIn = SslContextFactory.class.getClassLoader().getResourceAsStream(ksPath);
      ks.load(ksIn, password.toCharArray());

      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, password.toCharArray());

      // 信任库
      KeyStore tks = KeyStore.getInstance("jks");
      caIn = SslContextFactory.class.getClassLoader().getResourceAsStream(caPath);
      tks.load(caIn, password.toCharArray());

      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(tks);

      context = SSLContext.getInstance(PROTOCOL);
      // 初始化此上下文
      // 参数一：认证的密钥
      // 参数二：对等信任认证
      // 参数三：伪随机数生成器.
      context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    } catch (Exception e) {
      LOGGER.info(e.getMessage());
    } finally {
      try {
        if (ksIn != null) {
          ksIn.close();
        }
        if (caIn != null) {
          caIn.close();
        }
      } catch (IOException e) {
        LOGGER.info(e.getMessage());
      }
    }
    return context;
  }

}