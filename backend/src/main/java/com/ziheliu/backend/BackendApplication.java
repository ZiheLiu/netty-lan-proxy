package com.ziheliu.backend;

import com.ziheliu.common.SslContextFactory;
import com.ziheliu.common.config.Address;
import com.ziheliu.common.config.AddressEntry;
import com.ziheliu.common.config.Config;
import com.ziheliu.common.container.Container;
import com.ziheliu.common.container.ContainerHelper;
import com.ziheliu.common.factory.NettyFactory;
import com.ziheliu.common.protocol.ProxyDecoder;
import com.ziheliu.common.protocol.ProxyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutor;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendApplication implements Container {
  private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

  private final EventLoopGroup group = NettyFactory.getInstance().createEventLoopGroup(4);
  private final Bootstrap bootstrap = new Bootstrap();
  private final IdleCheckHandler idleCheckHandler = new IdleCheckHandler();

  public static void main(String[] args) throws InterruptedException {
    BackendApplication app = new BackendApplication();
    ContainerHelper.start(Collections.singletonList(app));
  }

  public void start() {
    bootstrap
        .group(group)
        .channel(NettyFactory.getInstance().getSocketChannelClass())
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
              // .addLast(new SslHandler(getEngine()))

              .addLast(new LengthFieldPrepender(2))
              .addLast(new ProxyEncoder())

              .addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS))
              .addLast(idleCheckHandler)

              .addLast(new ProxyDecoder())
              .addLast(new FrontendHandler());
          }
        });

    idleCheckHandler.setBootstrap(bootstrap);

    for (AddressEntry entry : Config.getInstance().getAddressEntries()) {
      Address address = Config.getInstance().getMainAddr();
      InetSocketAddress socketAddress = new InetSocketAddress(
          address.getHost(), address.getPort());

      for (EventExecutor executor : group) {
        ChannelFuture future = bootstrap.connect(socketAddress);
        Channel channel = future.channel();
        channel.attr(Constants.ADDRESS_ENTRY).set(entry);

        future.addListener(f -> {
          if (!f.isSuccess()) {
            LOGGER.warn("Connect to {}:{} failed, cause: {}",
                address.getHost(), address.getPort(), f.cause().getMessage());
            ((ChannelFuture) f).channel().pipeline().fireChannelInactive();
          }
        });
        // break;
      }
    }
  }

  @Override
  public void stop() {
    idleCheckHandler.stop();
    group.shutdownGracefully().syncUninterruptibly();
  }

  private SSLEngine getEngine() {
    SSLEngine engine = SslContextFactory.getClientContext().createSSLEngine();
    engine.setUseClientMode(true);
    return engine;
  }
}
