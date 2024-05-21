package com.kamilake.kamibotmc;

import org.slf4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kamilake.kamibotmc.KamibotLib.Async;
import com.mojang.logging.LogUtils;

public class EventHandler {

  JsonObject event = new JsonObject();

  public EventHandler set(String key, Object value) {
    event.addProperty(key, new Gson().toJson(value));
    return this;
  }

  public EventHandler set(String key, String value) {
    event.addProperty(key, value);
    return this;
  }

  public EventHandler set(String key, int value) {
    event.addProperty(key, value);
    return this;
  }

  public EventHandler set(String key, long value) {
    event.addProperty(key, value);
    return this;
  }

  private static final Logger LOGGER = LogUtils.getLogger();

  public void send() {
    Async.run(() -> {
      int uuid = Config.kamibotmcUuid;
      event.addProperty("uuid", uuid);
      // json 형식으로 이벤트 생성
      String json = new Gson().toJson(event);
      LOGGER.info("CommonEvent: " + json);
      WebsocketSender.sendMessage(json);
    });
  }
}
