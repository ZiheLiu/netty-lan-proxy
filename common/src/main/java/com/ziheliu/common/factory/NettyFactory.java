package com.ziheliu.common.factory;

import com.ziheliu.common.utils.OsUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NettyFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(NettyFactory.class);

  private static final NettyFactory instance;

  static {
    switch (OsUtil.osType) {
      case MaxOS:
        instance = new GenericNettyFactory();
        break;
      case Linux:
        instance = new LinuxNettyFactory();
        break;
      default:
        instance = new GenericNettyFactory();
    }
    LOGGER.info("osType: " + OsUtil.osType + ", instance: " + instance);
  }

  public static NettyFactory getInstance() {
    return instance;
  }

  public abstract Class<? extends ServerSocketChannel> getServerSocketChannelClass();

  public abstract Class<? extends SocketChannel> getSocketChannelClass();

  public abstract EventLoopGroup createEventLoopGroup(int threads);

  public abstract EventLoopGroup createEventLoopGroup();

  static class MaxOsNettyFactory extends NettyFactory {
    @Override
    public Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
      return KQueueServerSocketChannel.class;
    }

    @Override
    public Class<? extends SocketChannel> getSocketChannelClass() {
      return KQueueSocketChannel.class;
    }

    @Override
    public EventLoopGroup createEventLoopGroup(int threads) {
      return new KQueueEventLoopGroup(threads);
    }

    @Override
    public EventLoopGroup createEventLoopGroup() {
      return new KQueueEventLoopGroup();
    }
  }

  static class LinuxNettyFactory extends NettyFactory {
    @Override
    public Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
      return EpollServerSocketChannel.class;
    }

    @Override
    public Class<? extends SocketChannel> getSocketChannelClass() {
      return EpollSocketChannel.class;
    }

    @Override
    public EventLoopGroup createEventLoopGroup(int threads) {
      return new EpollEventLoopGroup(threads);
    }

    @Override
    public EventLoopGroup createEventLoopGroup() {
      return new EpollEventLoopGroup();
    }
  }

  static class GenericNettyFactory extends NettyFactory {
    @Override
    public Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
      return NioServerSocketChannel.class;
    }

    @Override
    public Class<? extends SocketChannel> getSocketChannelClass() {
      return NioSocketChannel.class;
    }

    @Override
    public EventLoopGroup createEventLoopGroup(int threads) {
      return new NioEventLoopGroup(threads);
    }

    @Override
    public EventLoopGroup createEventLoopGroup() {
      return new NioEventLoopGroup();
    }
  }
}
