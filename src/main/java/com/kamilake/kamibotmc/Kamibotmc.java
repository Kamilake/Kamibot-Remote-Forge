package com.kamilake.kamibotmc;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Kamibotmc.MODID)
public class Kamibotmc {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "kamibotmc";
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

  public Kamibotmc() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

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
    LOGGER.info("HELLO FROM COMMON SETUP@@@@@@@@@@@@@@@@@");
    LOGGER.info("Connecting " + Config.kamibotSocketUrl);
    LOGGER.info("uuid: " + Config.kamibotSocketUrl);
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
    String motd = event.getServer().getMotd(); // Get the server's MOTD
    LOGGER.info("Server Starting: " + motd);
    LOGGER.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + event.getServer().getServerModName());
    new EventHandler()
        .set("eventType", "ServerStartingEvent")
        .set("motd", motd)
        .send();
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
    LOGGER.info("CHAT >> {}", event.getMessage());
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
    LOGGER.info("LEVEL CHANGE >> {}", event.getEntity().getDisplayName().getString());
  }

  public static List<Player> players = new ArrayList<Player>();

  @SubscribeEvent
  public void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
    LOGGER.info("PLAYER CONNECT >> {}", event.getEntity().getDisplayName().getString());
    new EventHandler()
        .set("eventType", "PlayerLoggedInEvent")
        .set("playerName", event.getEntity().getDisplayName().getString())
        .set("playerUUID", event.getEntity().getUUID().toString())
        .send();
    players.add(event.getEntity());
  }

  @SubscribeEvent
  public void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
    LOGGER.info("PLAYER DISCONNECT >> {}", event.getEntity().getDisplayName().getString());
    new EventHandler()
        .set("eventType", "PlayerLoggedOutEvent")
        .set("playerName", event.getEntity().getDisplayName().getString())
        .set("playerUUID", event.getEntity().getUUID().toString())
        .send();
    players.remove(event.getEntity());
  }

  @SubscribeEvent
  public void onLivingDeath(LivingDeathEvent event) {
    if (event.getEntity() instanceof net.minecraft.world.entity.player.Player) {
      LOGGER.info("PLAYER DEATH >> {}", event.getEntity().getDisplayName().getString());
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
      LOGGER.info("DEATH MESSAGE >> {}", deathMessage);
      new EventHandler()
          .set("result", event.getResult().name())
          .set("eventType", "PlayerDeathEvent")
          .set("playerName", player)
          .set("killerName", killer)
          .set("damageSource", event.getSource().getMsgId())
          .set("playerPos", playerPosString)
          .set("deathMessage", deathMessage)
          .send();
    }
  }
}
