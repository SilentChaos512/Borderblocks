package net.silentchaos512.borderblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.borderblocks.client.ClientTickHandler;
import net.silentchaos512.borderblocks.client.ColorHandlers;
import net.silentchaos512.borderblocks.client.DebugDisplayHandler;
import net.silentchaos512.borderblocks.client.TextEffectDisplayHandler;
import net.silentchaos512.borderblocks.client.HudDisplayHandler;
import net.silentchaos512.borderblocks.client.key.KeyTracker;
import net.silentchaos512.lib.registry.SRegistry;

public class ClientProxy extends CommonProxy {

  @Override
  public void preInit(SRegistry registry, FMLPreInitializationEvent event) {

    super.preInit(registry, event);

    // Client-only event handlers
    MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
    MinecraftForge.EVENT_BUS.register(KeyTracker.INSTANCE);
    MinecraftForge.EVENT_BUS.register(new HudDisplayHandler());
    MinecraftForge.EVENT_BUS.register(TextEffectDisplayHandler.INSTANCE);
    MinecraftForge.EVENT_BUS.register(new DebugDisplayHandler());

    registry.clientPreInit();

    // Mod entity renderers?
  }

  @Override
  public void init(SRegistry registry, FMLInitializationEvent event) {

    super.init(registry, event);

    registry.clientInit();

    ColorHandlers.init();
  }

  public void postInit(SRegistry registry, FMLPostInitializationEvent event) {

    super.postInit(registry, event);

    registry.clientPostInit();
  }

  // @Override
  // public void spawnParticles(EnumModParticles type, Color color, World world, double x, double y,
  // double z, double motionX, double motionY, double motionZ) {
  //
  // Particle fx = null;
  //
  // float r = color.getRed();
  // float g = color.getGreen();
  // float b = color.getBlue();
  //
  // switch (type) {
  // case CURSED_HEART:
  // case ENCHANTED_HEART:
  // case HEART_CONTAINER:
  // fx = new ParticleSH(world, x, y, z, motionX, motionY, motionZ, 1.0f, 10, r, g, b);
  // break;
  // default:
  // throw new NotImplementedException("Unknown particle type: " + type);
  // }
  //
  // if (fx != null) {
  // Minecraft.getMinecraft().effectRenderer.addEffect(fx);
  // }
  // }

  @Override
  public EntityPlayer getClientPlayer() {

    return Minecraft.getMinecraft().player;
  }

  @Override
  public int getParticleSettings() {

    return Minecraft.getMinecraft().gameSettings.particleSetting;
  }
}
