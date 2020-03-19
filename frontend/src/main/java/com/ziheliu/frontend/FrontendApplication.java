package com.ziheliu.frontend;

import com.ziheliu.common.SslContextFactory;
import com.ziheliu.common.config.Address;
import com.ziheliu.common.config.AddressEntry;
import com.ziheliu.common.config.Config;
import com.ziheliu.common.container.Container;
import com.ziheliu.common.container.ContainerHelper;
import com.ziheliu.common.factory.NettyFactory;
import com.ziheliu.common.protocol.ProxyDecoder;
import com.ziheliu.common.protocol.ProxyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;

public class FrontendApplication implements Container {

  private EventLoopGroup bossGroup = NettyFactory.getInstance().createEventLoopGroup(2);
  private EventLoopGroup workerGroup = NettyFactory.getInstance().createEventLoopGroup();

  public static void main(String[] args) throws InterruptedException {
    FrontendApplication app = new FrontendApplication();
    ContainerHelper.start(Collections.singletonList(app));
  }

  @Override
  public void start() {
    startMainPort();
    startClientPorts();
  }

  @Override
  public void stop() {
    bossGroup.shutdownGracefully().syncUninterruptibly();
    workerGroup.shutdownGracefully().syncUninterruptibly();
  }

  private void startMainPort() {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap
        .group(bossGroup, workerGroup)
        .channel(NettyFactory.getInstance().getServerSocketChannelClass())
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
//              .addLast(new SslHandler(getEngine()))

              .addLast(new LengthFieldPrepender(4))
              .addLast(new ProxyEncoder())

              .addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
              .addLast(new IdleCheckHandler())

              .addLast(new LengthFieldBasedFrameDecoder(60 * 1024, 0, 4, 0, 4))
              .addLast(new ProxyDecoder())
              .addLast(new BackendHandler());
          }
        });

    Address address = Config.getInstance().getMainAddr();
    bootstrap.bind(new InetSocketAddress(address.getHost(), address.getPort()));
  }

  private void startClientPorts() {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap
        .group(bossGroup, workerGroup)
        .channel(NettyFactory.getInstance().getServerSocketChannelClass())
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
              .addLast(new ClientHandler());
          }
        });

    for (AddressEntry entry : Config.getInstance().getAddressEntries()) {
      Address address = entry.getFrontendAddr();
      bootstrap.bind(new InetSocketAddress(address.getHost(), address.getPort()));
    }
  }

  private SSLEngine getEngine() {
    SSLEngine engine = SslContextFactory.getServerContext().createSSLEngine();
    engine.setUseClientMode(false);
    engine.setNeedClientAuth(true);
    return engine;
  }
}
