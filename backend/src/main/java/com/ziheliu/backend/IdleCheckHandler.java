package com.ziheliu.backend;

import com.ziheliu.common.config.Address;
import com.ziheliu.common.config.AddressEntry;
import com.ziheliu.common.config.Config;
import com.ziheliu.common.protocol.ProxyMessage;
import com.ziheliu.common.protocol.ProxyMessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class IdleCheckHandler extends ChannelInboundHandlerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(IdleCheckHandler.class);

  private static final ProxyMessage HEART_MSG = new ProxyMessage(
      ProxyMessageType.BACKEND_HEART_BEAT, 0, Unpooled.EMPTY_BUFFER);

  private static final int MAX_ATTEMPTS = 128;

  private volatile boolean reconnect = true;
  private final AtomicInteger attempts = new AtomicInteger();

  private final Timer timer = new HashedWheelTimer();
  private AtomicReference<Bootstrap> bootstrapRef = new AtomicReference<>();

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleState state = ((IdleStateEvent) evt).state();
      if (state == IdleState.WRITER_IDLE) {
        ctx.writeAndFlush(HEART_MSG);
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    attempts.set(0);

    AddressEntry entry = ctx.channel().attr(Constants.ADDRESS_ENTRY).get();

    LOGGER.info("Connect to frontend to watch {}:{}",
        entry.getFrontendAddr().getHost(),
        entry.getFrontendAddr().getPort());

    ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
    buf.writeBytes(Config.getInstance().getPassword().getBytes(CharsetUtil.UTF_8));

    ProxyMessage msg = new ProxyMessage(
        ProxyMessageType.BACKEND_CONNECT,
        entry.getFrontendAddr().getPort(),
        buf);
    ctx.writeAndFlush(msg);

    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    LOGGER.info("Connection to frontend is closed.");

    if (!reconnect) {
      return;
    }

    int attemptsVal = attempts.getAndIncrement();
    if (attemptsVal < MAX_ATTEMPTS) {
      LOGGER.info("Attempt#{} to reconnect to frontend.", attempts);

      int timeout = 2 << attemptsVal;

      timer.newTimeout(t -> {
        Address address = Config.getInstance().getMainAddr();
        InetSocketAddress socketAddress = new InetSocketAddress(
            address.getHost(), address.getPort());

        while (bootstrapRef.get() == null) {

        }

        ChannelFuture future = bootstrapRef.get().connect(socketAddress);

        future.addListener(f -> {
          AddressEntry entry = ctx.channel().attr(Constants.ADDRESS_ENTRY).get();
          Channel channel = ((ChannelFuture) f).channel();
          channel.attr(Constants.ADDRESS_ENTRY).set(entry);

          if (!f.isSuccess()) {
            LOGGER.warn("Connect to {}:{} failed, cause: {}",
                address.getHost(), address.getPort(), f.cause().getMessage());
            ((ChannelFuture) f).channel().pipeline().fireChannelInactive();
          }
        });

      }, timeout, TimeUnit.MILLISECONDS);
    }

    super.channelInactive(ctx);
  }

  public void setBootstrap(Bootstrap bootstrap) {
    bootstrapRef.set(bootstrap);
  }

  public void stop() {
    reconnect = false;
  }
}
