package com.ziheliu.common.container;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainerHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ContainerHelper.class);

  private static List<Container> containers;

  private static volatile boolean running = true;

  public static void start(List<Container> containers) {

    ContainerHelper.containers = containers;

    startContainers();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      synchronized (ContainerHelper.class) {
        stopContainers();
        running = false;
        ContainerHelper.class.notify();
      }
    }));

    synchronized (ContainerHelper.class) {
      while (running) {
        try {
          ContainerHelper.class.wait();
        } catch (InterruptedException e) {
          LOGGER.info("Containers has been interrupted");
          System.exit(0);
        }
      }
    }

  }

  private static void startContainers() {
    for (Container container : containers) {
      LOGGER.info("Starting container [{}]", container.getClass().getName());
      container.start();
      LOGGER.info("Started container [{}]", container.getClass().getName());
    }
  }

  private static void stopContainers() {
    for (Container container : containers) {
      LOGGER.info("Stopping container [{}]", container.getClass().getName());
      container.stop();
      LOGGER.info("Stopped container [{}]", container.getClass().getName());
    }
  }
}
