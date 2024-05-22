package com.kamilake.kamibotmc;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kamilake.kamibotmc.KamibotLib.Async;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.server.ServerLifecycleHooks;
import java.net.URI;
import java.util.concurrent.TimeUnit;



public class WebsocketSender extends WebSocketClient {

  public static WebsocketSender instance;
  private static final Logger LOGGER = LogUtils.getLogger();

  public WebsocketSender(URI serverUri) {
    super(serverUri);
    instance = this;
  }

  public static void sendMessage(String message) {
    try {
      if (instance == null) {
        instance = new WebsocketSender(URI.create(Config.kamibotSocketUrl));
        LOGGER.info("Connecting to websocket server... (" + Config.kamibotSocketUrl + ")");
        instance.connectBlocking(5, TimeUnit.SECONDS);
        instance.send("{\"eventType\":\"auth\",\"uuid\":" + Config.kamibotmcUuid + "}");
      }
      instance.send(message);
    } catch (WebsocketNotConnectedException e) {
      LOGGER.error("Failed to send message: " + message + " because the websocket is not connected.");
      instance = null;
    } catch (InterruptedException e) {
      LOGGER.error("Failed to connect to websocket server. (InterruptedException)");
      instance = null;
    }
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    LOGGER.info("Connected");
  }

  @Override
  public void onMessage(String json) {
    LOGGER.info("Received: " + json);
    Async.run(() -> {
      JSONObject messageObject = new JSONObject(json);
      String eventType = messageObject.getString("eventType");
      switch (eventType) {
        case "SendChatEvent":
          sendChatToMinecraft(messageObject.getString("message"));
          break;
        default:
          LOGGER.warn("Unknown event type: " + eventType);
      }
    });
  }

  private void sendChatToMinecraft(String string) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    PlayerList playerList = server.getPlayerList();
    for (ServerPlayer player : playerList.getPlayers()) {
      player.sendSystemMessage(Component.literal(string));
    }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    LOGGER.info("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
  }

  @Override
  public void onError(Exception ex) {
    ex.printStackTrace();
  }
}
