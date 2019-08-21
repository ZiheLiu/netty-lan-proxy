package com.ziheliu.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class Config {
  private static Logger LOGGER = LoggerFactory.getLogger(Config.class);

  private static final String DEFAULT_CONFIG_FILE_NAME = "config.yaml";

  private static Config instance;

  static {
    InputStream fin = null;
    try {
      fin = Config.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_NAME);
      Yaml yaml = new Yaml();
      instance = yaml.loadAs(fin, Config.class);
    } catch (Exception e) {
      LOGGER.error("Load config yaml failed, error: " + e.getMessage());
      System.exit(-1);
    } finally {
      if (fin != null) {
        try {
          fin.close();
        } catch (IOException e) {
          LOGGER.error("Close config yaml fialed, error: " + e.getMessage());
          System.exit(-1);
        }
      }
    }
  }

  private Address mainAddr;

  private List<AddressEntry> addressEntries;

  public static Config getInstance() {
    return instance;
  }

  public Address getMainAddr() {
    return mainAddr;
  }

  public void setMainAddr(Address mainAddr) {
    this.mainAddr = mainAddr;
  }

  public List<AddressEntry> getAddressEntries() {
    return addressEntries;
  }

  public void setAddressEntries(List<AddressEntry> addressEntries) {
    this.addressEntries = addressEntries;
  }
}
