package com.kamilake.kamibot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = KamibotRemote.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  private static final ForgeConfigSpec.ConfigValue<String> KAMIBOT_SOCKET_URL = BUILDER
      .comment("카미봇 서버 URL (string)")
      .define("kamibotSocketUrl", "wss://kamibot.kami.live/minecraftForge/ws");

  private static final ForgeConfigSpec.IntValue KAMIBOT_REMOTE_UUID = BUILDER
      .comment("이 마인크래프트 서버를 카미봇이 식별하기 위한 고유 식별자 (int)")
      .defineInRange("uuid", new Random().nextInt(Integer.MAX_VALUE), 0, Integer.MAX_VALUE);

  private static final ForgeConfigSpec.BooleanValue ENABLE_VARBOSE_LOGGING = BUILDER
      .comment("디버깅을 위한 상세 로깅 활성화 (boolean)")
      .define("enableVerboseLogging", false);

  // a list of strings that are treated as resource locations for items
  private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
      .comment("A list of items to log on common setup.")
      .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);


  static final ForgeConfigSpec SPEC = BUILDER.build();

  public static int magicNumber;
  public static String kamibotSocketUrl;
  public static int kamibotRemoteUuid;
  public static boolean enableVerboseLogging;
  public static Set<Item> items;

  private static boolean validateItemName(final Object obj) {
    return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
  }

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    kamibotSocketUrl = KAMIBOT_SOCKET_URL.get();
    kamibotRemoteUuid = KAMIBOT_REMOTE_UUID.get();
    enableVerboseLogging = ENABLE_VARBOSE_LOGGING.get();

    // convert the list of strings into a set of items
    items = ITEM_STRINGS.get().stream()
        .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
        .collect(Collectors.toSet());
  }
}
