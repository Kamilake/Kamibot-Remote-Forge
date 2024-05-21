package com.kamilake.kamibotmc;

import org.slf4j.Logger;
import com.google.gson.Gson;
import com.kamilake.kamibotmc.KamibotLib.Async;
import com.mojang.logging.LogUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// CommonHandler로 여러 가지 형식의 이벤트를 보내기 위해 통합 이벤트 생성
@Getter
@Setter
@Builder
@Deprecated
public class CommonEvent {

  String eventType;
  String displayMessage;
  String displayTitle;
  String displayFooter;
  String playerName;
  String playerUUID;
  boolean isEmbed;
  int uuid; // UUID 필드 추가

  private static final Logger LOGGER = LogUtils.getLogger();
  @Deprecated
  public void send() {
    Async.run(() -> {
      this.uuid = Config.kamibotmcUuid; // UUID 설정
      // json 형식으로 이벤트 생성
      Gson gson = new Gson();
      String json = gson.toJson(this);
      LOGGER.info("CommonEvefffnt: " + json);
    });
  }
}
