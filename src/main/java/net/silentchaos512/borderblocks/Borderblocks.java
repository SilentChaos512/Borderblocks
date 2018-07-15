package net.silentchaos512.borderblocks;

import net.minecraft.creativetab.CreativeTabs;
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
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.network.*;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.LogHelper;

import java.util.Random;

@Mod(modid = Borderblocks.MOD_ID, name = Borderblocks.MOD_NAME, version = Borderblocks.VERSION, dependencies = Borderblocks.DEPENDENCIES, guiFactory = "net.silentchaos512.borderblocks.client.gui.GuiFactoryBB")
public class Borderblocks {

    public static final String MOD_ID = "borderblocks";
    public static final String MOD_NAME = "Borderblocks";
    public static final String VERSION = "0.1.4";
    public static final String VERSION_SILENTLIB = "2.3.8";
    public static final int BUILD_NUM = 0;
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:silentlib@[" + VERSION_SILENTLIB + ",);";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public static Random random = new Random();
    public static LogHelper log = new LogHelper(MOD_NAME, BUILD_NUM);
    public static LocalizationHelper localization;

    public static SRegistry registry = new SRegistry(MOD_ID, log);
    public static NetworkHandlerSL network;

    public static CreativeTabs creativeTab = registry.makeCreativeTab(MOD_ID, ProgressionTier.ERIDIUM::getRelicStack);

    @Instance(MOD_ID)
    public static Borderblocks instance;

    @SidedProxy(clientSide = "net.silentchaos512.borderblocks.ClientProxy", serverSide = "net.silentchaos512.borderblocks.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        localization = new LocalizationHelper(MOD_ID).setReplaceAmpersand(true);
        SilentLib.instance.registerLocalizationHelperForMod(MOD_ID, localization);

        registry.recipes.setJsonHellMode(0 == getBuildNum());

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

    public int getBuildNum() {
        return BUILD_NUM;
    }
}
