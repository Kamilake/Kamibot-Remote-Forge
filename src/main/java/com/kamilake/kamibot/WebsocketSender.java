package com.kamilake.kamibot;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kamilake.kamibot.KamibotLib.Async;
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
        if (Config.enableVerboseLogging)
          LOGGER.info("Connecting to websocket server... (" + Config.kamibotSocketUrl + ")");
        instance.connectBlocking(10, TimeUnit.SECONDS);
        instance.send("{\"eventType\":\"auth\",\"uuid\":" + Config.kamibotRemoteUuid + "}");
      }
      instance.send(message);
    } catch (InterruptedException | WebsocketNotConnectedException e) {
      if (Config.enableVerboseLogging)
        LOGGER.error("Failed to send message: " + message + " because the websocket is not connected.");
      instance = new WebsocketSender(URI.create(Config.kamibotSocketUrl));
      if (Config.enableVerboseLogging)
        LOGGER.info("Reconnecting to websocket server... (" + Config.kamibotSocketUrl + ")");
      try {
        instance.connectBlocking(10, TimeUnit.SECONDS);
      } catch (InterruptedException e1) {
      }
      instance.send("{\"eventType\":\"auth\",\"uuid\":" + Config.kamibotRemoteUuid + "}");
      instance.send(message);
    }
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    if (Config.enableVerboseLogging)
      LOGGER.info("Connected");
  }

  @Override
  public void onMessage(String json) {
    if (Config.enableVerboseLogging)
      LOGGER.info("Received: " + json);
    Async.run(() -> {
      JSONObject messageObject = new JSONObject(json);
      String eventType = messageObject.getString("eventType");
      switch (eventType) {
        case "SendChatEvent":
          sendChatToMinecraft(messageObject.getString("message"));
          break;
        case "SendTellEvent":
          sendTellToMinecraft(messageObject.getString("message"), messageObject.getString("user"));
          break;
        default:
          LOGGER.warn("Unknown event type: " + eventType);
      }
    });
  }

  private void sendChatToMinecraft(String content) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    PlayerList playerList = server.getPlayerList();
    for (ServerPlayer player : playerList.getPlayers()) {
      player.sendSystemMessage(Component.literal(content));
    }
  }

  private void sendTellToMinecraft(String content, String user) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    PlayerList playerList = server.getPlayerList();
    playerList.getPlayers().stream()
        .filter(player -> player.getUUID().toString().equals(user) || player.getDisplayName().getString().equals(user))
        .forEach(player -> player.sendSystemMessage(Component.literal(content)));
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    LOGGER.info("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
    // Try to reconnect
    for (int i = 0; i < 10; i++) {
      try {
        Thread.sleep(900);
        if (Config.enableVerboseLogging)
          LOGGER.info("Reconnecting to websocket server... (" + Config.kamibotSocketUrl + ")");
        instance = new WebsocketSender(URI.create(Config.kamibotSocketUrl));
        instance.connectBlocking(10, TimeUnit.SECONDS);
        instance.send("{\"eventType\":\"auth\",\"uuid\":" + Config.kamibotRemoteUuid + "}");
        break;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onError(Exception ex) {
    ex.printStackTrace();
  }
}
