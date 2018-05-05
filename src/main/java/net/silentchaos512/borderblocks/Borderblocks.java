package net.silentchaos512.borderblocks;

import java.util.Random;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.borderblocks.command.CommandLevel;
import net.silentchaos512.borderblocks.init.ModItems;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.network.MessageDataSync;
import net.silentchaos512.borderblocks.network.MessagePlaySound;
import net.silentchaos512.borderblocks.network.MessageSelectClass;
import net.silentchaos512.borderblocks.network.MessageSkillRespec;
import net.silentchaos512.borderblocks.network.MessageSkillSetPoints;
import net.silentchaos512.borderblocks.network.MessageUseActionSkill;
import net.silentchaos512.borderblocks.network.MessageXpGain;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.creativetab.CreativeTabSL;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.LogHelper;

@Mod(modid = Borderblocks.MOD_ID, name = Borderblocks.MOD_NAME, version = Borderblocks.VERSION, dependencies = Borderblocks.DEPENDENCIES, guiFactory = "net.silentchaos512.borderblocks.client.gui.GuiFactoryBB")
public class Borderblocks {

  public static final String MOD_ID = "borderblocks";
  public static final String MOD_NAME = "Borderblocks";
  public static final String VERSION = "@VERSION@";
  public static final String VERSION_SILENTLIB = "SL_VERSION";
  public static final int BUILD_NUM = 0;
  public static final String DEPENDENCIES = "required-after:silentlib@[" + VERSION_SILENTLIB + ",);";
  public static final String RESOURCE_PREFIX = MOD_ID + ":";

  public static Random random = new Random();
  public static LogHelper log = new LogHelper(MOD_NAME, BUILD_NUM);
  public static LocalizationHelper localization;

  public static SRegistry registry = new SRegistry(MOD_ID, log);
  public static NetworkHandlerSL network;

  public static CreativeTabSL CREATIVE_TAB = new CreativeTabSL(MOD_ID, ModItems.progressionRelic, ProgressionTier.ERIDIUM.ordinal());

  @Instance(MOD_ID)
  public static Borderblocks instance;

  @SidedProxy(clientSide = "net.silentchaos512.borderblocks.ClientProxy", serverSide = "net.silentchaos512.borderblocks.CommonProxy")
  public static CommonProxy proxy;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    localization = new LocalizationHelper(MOD_ID).setReplaceAmpersand(true);
    SilentLib.instance.registerLocalizationHelperForMod(MOD_ID, localization);

    network = new NetworkHandlerSL(MOD_ID);
    network.register(MessageDataSync.class, Side.CLIENT);
    network.register(MessageSkillSetPoints.class, Side.SERVER);
    network.register(MessageSkillRespec.class, Side.SERVER);
    network.register(MessageSelectClass.class, Side.SERVER);
    network.register(MessageUseActionSkill.class, Side.SERVER);
    network.register(MessagePlaySound.class, Side.CLIENT);
    network.register(MessageXpGain.class, Side.CLIENT);

    proxy.preInit(registry, event);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {

    proxy.init(registry, event);
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    proxy.postInit(registry, event);
  }

  @EventHandler
  public void onServerLoad(FMLServerStartingEvent event) {

    event.registerServerCommand(new CommandLevel());
  }
}
