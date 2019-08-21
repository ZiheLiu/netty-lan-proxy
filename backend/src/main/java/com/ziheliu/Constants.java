package com.ziheliu;

import com.ziheliu.common.config.AddressEntry;
import io.netty.util.AttributeKey;

public interface Constants {

  public static final
      AttributeKey<AddressEntry> ADDRESS_ENTRY = AttributeKey.newInstance("ADDRESS_ENTRY");
}
