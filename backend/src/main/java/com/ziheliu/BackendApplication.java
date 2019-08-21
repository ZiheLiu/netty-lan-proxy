package com.ziheliu;

import com.ziheliu.common.config.Address;
import com.ziheliu.common.config.AddressEntry;
import com.ziheliu.common.config.Config;
import com.ziheliu.common.container.Container;
import com.ziheliu.common.container.ContainerHelper;
import com.ziheliu.common.protocol.ProxyDecoder;
import com.ziheliu.common.protocol.ProxyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import java.net.InetSocketAddress;
import java.util.Collections;

public class BackendApplication implements Container {
  private EventLoopGroup group;

  public void start() {
    group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap
      .group(group)
      .channel(NioSocketChannel.class)
      .handler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          ch.pipeline()
            .addLast(new LengthFieldPrepender(4))
            .addLast(new ProxyEncoder())

            .addLast(new LengthFieldBasedFrameDecoder(60 * 1024, 0, 4, 0, 4))
            .addLast(new ProxyDecoder())
            .addLast(new FrontendHandler());
        }
      });

    for (AddressEntry entry : Config.getInstance().getAddressEntries()) {
      Address address = Config.getInstance().getMainAddr();
      ChannelFuture future = bootstrap.connect(new InetSocketAddress(address.getHost(), address.getPort()));
      future.addListener(f -> {
        if (f.isSuccess()) {
          Channel channel = ((ChannelFuture) f).channel();
          channel.attr(Constants.ADDRESS_ENTRY).set(entry);
        }
      });

    }
  }

  @Override
  public void stop() {
    group.shutdownGracefully().syncUninterruptibly();
  }

  public static void main(String[] args) throws InterruptedException {
    BackendApplication app = new BackendApplication();
    ContainerHelper.start(Collections.singletonList(app));
  }
}
