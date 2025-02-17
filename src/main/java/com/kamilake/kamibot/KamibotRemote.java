package com.kamilake.kamibot;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.server.ServerLifecycleHooks;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(KamibotRemote.MODID)
public class KamibotRemote {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "kamibot_remote";
  // Directly reference a slf4j logger
  private static final Logger LOGGER = LogUtils.getLogger();
  // // Create a Deferred Register to hold Blocks which will all be registered
  // under the "examplemod" namespace
  // public static final DeferredRegister<Block> BLOCKS =
  // DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
  // // Create a Deferred Register to hold Items which will all be registered
  // under the "examplemod" namespace
  // public static final DeferredRegister<Item> ITEMS =
  // DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
  // // Create a Deferred Register to hold CreativeModeTabs which will all be
  // registered under the "examplemod" namespace
  // public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
  // DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

  // // Creates a new Block with the id "examplemod:example_block", combining the
  // namespace and path
  // public static final RegistryObject<Block> EXAMPLE_BLOCK =
  // BLOCKS.register("example_block", () -> new
  // Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
  // // Creates a new BlockItem with the id "examplemod:example_block", combining
  // the namespace and path
  // public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM =
  // ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new
  // Item.Properties()));

  // // Creates a new food item with the id "examplemod:example_id", nutrition 1
  // and saturation 2
  // public static final RegistryObject<Item> EXAMPLE_ITEM =
  // ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new
  // FoodProperties.Builder()
  // .alwaysEat().nutrition(1).saturationMod(2f).build())));

  // // Creates a creative tab with the id "examplemod:example_tab" for the
  // example item, that is placed after the combat tab
  // public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB =
  // CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
  // .withTabsBefore(CreativeModeTabs.COMBAT)
  // .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
  // .displayItems((parameters, output) -> {
  // output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For
  // your own tabs, this method is preferred over the event
  // }).build());

  public KamibotRemote() {

    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    if (FMLLoader.getDist() == Dist.CLIENT) return; // 클라이언트에서 실행되면 초기화를 중단합니다.
    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    // // Register the Deferred Register to the mod event bus so blocks get
    // registered
    // BLOCKS.register(modEventBus);
    // // Register the Deferred Register to the mod event bus so items get
    // registered
    // ITEMS.register(modEventBus);
    // // Register the Deferred Register to the mod event bus so tabs get registered
    // CREATIVE_MODE_TABS.register(modEventBus);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);

    // // Register the item to a creative tab
    // modEventBus.addListener(this::addCreative);

    // Register our mod's ForgeConfigSpec so that Forge can create and load the
    // config file for us
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // Some common setup code
    LOGGER.info("====Kamibot Remote====");
    LOGGER.info("Connecting " + Config.kamibotSocketUrl + "...");
    LOGGER.info("UUID: " + Config.kamibotSocketUrl);
    // if (Config.logDirtBlock)
    // LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

    // LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

    // Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
  }

  // // Add the example block item to the building blocks tab
  // private void addCreative(BuildCreativeModeTabContentsEvent event)
  // {
  // if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
  // event.accept(EXAMPLE_BLOCK_ITEM);
  // }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    final String motd;// = "Forge Server"; // Default MOTD
    {
      boolean a;
      try {
        event.getServer().getMotd(); // Get the server's MOTD
        a = false;
      } catch (NoSuchMethodError e) {
        a = true;
      }
      if (a) {
        LOGGER.error(
            "Failed to get server MOTD due to NoSuchMethodError. This is likely due to a version mismatch between the mod and Forge. Please download a matching version from the official GitHub page.");
        LOGGER.error("Motd를 찾을 수 없는데, 모드 오류가 아니라 아마 모드 버전과 Forge 버전이 호환되지 않아서 그럴 수도 있어요. 공식 GitHub 페이지에서 일치하는 버전을 다운로드 받아보세요.");
        motd = "Forge Server";
      } else {
        motd = event.getServer().getMotd();
      }
    }
    RootCommandNode<CommandSourceStack> commandNode = event.getServer().getCommands().getDispatcher().getRoot();
    commandNode.addChild(Commands.literal("kamibot-info")
        // .requires((commandSource) -> commandSource.hasPermission(2)) // 권한이 필요한 경우
        .requires((commandSource) -> true) // 권한이 필요하지 않은 경우
        .executes((commandContext) -> {
          commandContext.getSource().sendSuccess(() -> {
            String output = "====Kamibot Remote 정보====\n";
            output += "서버: " + motd + "\n";
            output += "UUID: " + Config.kamibotRemoteUuid + "\n";
            output += "소켓: " + Config.kamibotSocketUrl + "\n";
            long delay = measureHttpGetDelay();
            output += "소켓 연결 상태: " + WebsocketSender.instance == null
              ? "소켓 없음"
              : WebsocketSender.instance.isOpen() ? "연결됨, Https (" + (delay == -1 ? "측정 불가능" : delay + "ms") + ")" : "연결 끊김";
            return Component.literal(output);
          }, false);
          return 1;
        }).build());

    commandNode.addChild(Commands.literal("kamibot-register")
        .requires((commandSource) -> commandSource.hasPermission(2))
        .then(Commands.argument("url", StringArgumentType.string())
            .executes((commandContext) -> {
              String url = StringArgumentType.getString(commandContext, "url");
              // URL 등록 로직
              // https://discord.com/channels/996780771564081262/996984390833938482

              String[] urlParts = url.split("/");
              if (urlParts.length < 6 || !urlParts[2].equals("discord.com") || !urlParts[3].equals("channels")) {
                commandContext.getSource()
                    .sendFailure(Component.literal(
                        "올바른 Discord 채널 링크를 입력하세요.\n 예시: /kamibot-register \"https://discord.com/channels/996780771564081262/996953596329476126\""));
                return 1;
              }
              String guildId = urlParts[4];
              String channelId = urlParts[5];

              ServerPlayer player;
              String playerName, playerUUID;
              try {
                player = commandContext.getSource().getPlayerOrException();
                playerName = player.getDisplayName().getString();
                playerUUID = player.getUUID().toString();
              } catch (Exception e) {
                player = null;
                playerName = null;
                playerUUID = null;
              }

              new EventHandler()
                  .set("eventType", "RegisterChannelEvent")
                  .set("guildId", guildId)
                  .set("channelId", channelId)
                  .set("serverName", motd)
                  .set("uuid", Config.kamibotRemoteUuid)
                  .set("playerName", playerName)
                  .set("playerUUID", playerUUID)
                  .send();

              commandContext.getSource().sendSuccess(() -> Component.literal("등록 요청에 성공했어요! 해당 채널에서 카미봇이 보낸 메세지를 확인해보세요."), false);
              return 1;
            }))
        .executes((commandContext) -> {

          commandContext.getSource().sendFailure(Component.literal("사용법: /kamibot-register \"<채널 링크>\"\n연동하고 싶은 Discord 채널을 우클릭한 다음 '링크 복사하기'를 누르세요."));
          return 1;
        }).build());

    commandNode.addChild(Commands.literal("discord") // Discord 계정 통합
        .requires((commandSource) -> true)
        .then(Commands.argument("id", StringArgumentType.string())
            .executes((commandContext) -> {
              String id = StringArgumentType.getString(commandContext, "id");
              ServerPlayer player;
              try {
                player = commandContext.getSource().getPlayerOrException();
              } catch (Exception e) {
                commandContext.getSource().sendFailure(Component.literal("이 명령어는 인게임에서만 사용할 수 있어요."));
                return 1;
              }
              new EventHandler()
                  .set("eventType", "DiscordIntegrationEvent")
                  .set("discordId", id)
                  .set("serverName", motd)
                  .set("serverUuid", Config.kamibotRemoteUuid)
                  .set("playerName", player.getDisplayName().getString())
                  .set("playerUUID", player.getUUID().toString())
                  .send();

              commandContext.getSource().sendSuccess(() -> Component.literal("등록 요청에 성공했어요! 카미봇의 DM에서 카미봇이 보낸 메세지를 확인해보세요. (시간 제한 5분)"), false);
              return 1;
            }))
        .executes((commandContext) -> {
          commandContext.getSource().sendSuccess(() -> Component.literal(
              "====Kamibot Remote 도움말====\n\n내 Minecraft 계정과 Discord 계정을 같은 이름으로 표시해주는 기능이에요.\n사용법: /discord <내 디스코드 계정>\n이렇게 하면 여러분의 메세지가 Discord 채널에서 올바른 이름으로 표시돼요!\n예시: /discord kamilake\n예시: /discord 1019061779357245521"),
              false);
          return 1;
        }).build());

    String releaseVersion = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
    if (releaseVersion == null) {
      releaseVersion = "DEV";
    }

    new EventHandler()
        .set("eventType", "ServerStartingEvent")
        .set("motd", motd)
        .set("releaseVersion", releaseVersion)
        .send();
  }

  public long measureHttpGetDelay() {
    try {
      URL url = new URI("https://kamibot.app/api").toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      connection.setRequestMethod("GET");
      connection.setConnectTimeout(1000); // Set timeout to 1000ms
      connection.setReadTimeout(1000); // Set read timeout to 1000ms

      long startTime = System.currentTimeMillis();
      int responseCode = connection.getResponseCode();
      long endTime = System.currentTimeMillis();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        return endTime - startTime;
      } else {
        return -1;
      }
    } catch (Exception e) {
      return -1;
    }
  }

  // // You can use EventBusSubscriber to automatically register all static
  // methods in the class annotated with @SubscribeEvent
  // @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD,
  // value = Dist.CLIENT)
  // public static class ClientModEvents
  // {
  // @SubscribeEvent
  // public static void onClientSetup(FMLClientSetupEvent event)
  // {
  // // Some client setup code
  // LOGGER.info("HELLO FROM CLIENT SETUP");
  // LOGGER.info("MINECRAFT NAME >> {}",
  // Minecraft.getInstance().getUser().getName());
  // }
  // }

  // onServerChatEvent
  @SubscribeEvent
  public void onServerChatEvent(ServerChatEvent event) {
    // LOGGER.info("CHAT >> {}", event.getMessage());
    new EventHandler()
        .set("eventType", "ServerChatEvent")
        .set("message", event.getMessage().getString())
        .set("playerName", event.getPlayer().getDisplayName().getString())
        .set("playerUUID", event.getPlayer().getUUID().toString())
        .send();
    event.getPlayer();
  }

  // @SubscribeEvent
  // public void onAdvancementGrant(AdvancementEvent.AdvancementEarnEvent event) {
  // LOGGER.info("ADVANCEMENT >> {}", event.getAdvancement().getId());
  // }


  // @SubscribeEvent
  // public void onServerExit() {
  // LOGGER.info("SERVER STOPPING"); // 작동 안됨
  // }

  @SubscribeEvent
  public void onPlayerLevelChange(PlayerXpEvent.LevelChange event) {
    // LOGGER.info("LEVEL CHANGE >> {}", event.getEntity().getDisplayName().getString());
  }

  @SubscribeEvent
  public void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
    // LOGGER.info("PLAYER CONNECT >> {}", event.getEntity().getDisplayName().getString());
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

    new EventHandler()
        .set("eventType", "PlayerLoggedInEvent")
        .set("playerName", event.getEntity().getDisplayName().getString())
        .set("playerUUID", event.getEntity().getUUID().toString())
        .set("playerCount", server.getPlayerCount())
        .set("maxPlayerCount", server.getMaxPlayers())
        .send();
  }

  @SubscribeEvent
  public void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
    // LOGGER.info("PLAYER DISCONNECT >> {}", event.getEntity().getDisplayName().getString());
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    new EventHandler()
        .set("eventType", "PlayerLoggedOutEvent")
        .set("playerName", event.getEntity().getDisplayName().getString())
        .set("playerUUID", event.getEntity().getUUID().toString())
        .set("playerCount", server.getPlayerCount() - 1)
        .set("maxPlayerCount", server.getMaxPlayers())
        .send();
  }

  @SubscribeEvent
  public void onLivingDeath(LivingDeathEvent event) {
    if (event.getEntity() instanceof net.minecraft.world.entity.player.Player) {
      // LOGGER.info("PLAYER DEATH >> {}", event.getEntity().getDisplayName().getString());
      String player = event.getEntity().getDisplayName().getString();
      String killer;
      Entity killerEntity = event.getSource().getEntity();
      if (killerEntity == null) {
        killer = "environment";
      } else {
        killer = killerEntity.getDisplayName().getString();
      }
      Vec3 playerPos = event.getEntity().position();
      String playerPosString =
          String.format("%.2f", playerPos.x) + ", "
              + String.format("%.2f", playerPos.y) + ", "
              + String.format("%.2f", playerPos.z);
      String deathMessage = player + " was killed by " + killer;
      // LOGGER.info("DEATH MESSAGE >> {}", deathMessage);
      new EventHandler()
          .set("result", event.getResult().name())
          .set("eventType", "PlayerDeathEvent")
          .set("playerName", player)
          .set("killerName", killer)
          .set("damageSource", event.getSource().getMsgId())
          .set("playerPos", playerPosString)
          .set("playerUUID", event.getEntity().getUUID().toString())
          .set("deathMessage", deathMessage)
          .send();
    }
  }

}
