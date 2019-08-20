package com.ziheliu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class FrontendApplication {

  public FrontendApplication() {
  }

  public static void main(String[] args) throws InterruptedException {
    new FrontendApplication().start();
  }

  public void start() throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap
          .group(group)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              InetSocketAddress address = ch.localAddress();
              if (address.getPort() == Config.getInstance().getPort()) {
                ch.pipeline()
                  .addLast(new FrontendCreateChannelHandler());
              } else {
                ch.pipeline()
                  .addLast(new ClientReadHandler());
              }
            }
          });

      List<ChannelFuture> futures = new ArrayList<>();
      futures.add(bootstrap.bind(Config.getInstance().getPort()));
      for (Integer port : Config.getInstance().getAddressMap().keySet()) {
        futures.add(bootstrap.bind(port));
      }

      for (ChannelFuture future : futures) {
        future.sync();
      }

      for (ChannelFuture future : futures) {
        future.channel().closeFuture().sync();
      }

    } finally {
      group.shutdownGracefully().sync();
    }

  }
}
