package com.ziheliu;

import com.ziheliu.protocol.RpDecoder;
import com.ziheliu.protocol.RpEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import java.net.InetSocketAddress;

public class BackendApplication {
  private final String host;
  private final int port;

  public BackendApplication(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void start() throws InterruptedException {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap
          .group(group)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline()
                .addLast(new LengthFieldPrepender(4))
                .addLast(new RpEncoder())

                .addLast(new LengthFieldBasedFrameDecoder(60 * 1024, 0, 4, 0, 4))
                .addLast(new RpDecoder())
                .addLast(new FrontendReadHandler());
            }
          });

      ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
      future.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    new BackendApplication("localhost", 8000).start();
  }
}
