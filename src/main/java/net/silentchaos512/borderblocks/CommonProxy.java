package net.silentchaos512.borderblocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.silentchaos512.borderblocks.advancements.ModTriggers;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.event.CommonEvents;
import net.silentchaos512.borderblocks.event.SkillEvents;
import net.silentchaos512.borderblocks.init.ModBlocks;
import net.silentchaos512.borderblocks.init.ModItems;
import net.silentchaos512.borderblocks.init.ModSounds;
import net.silentchaos512.borderblocks.lib.character.CharacterClass;
import net.silentchaos512.borderblocks.lib.skill.BlockDropsSkill;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.network.MessagePlaySound;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.XPManager;
import net.silentchaos512.borderblocks.world.WorldGeneratorBB;
import net.silentchaos512.lib.registry.SRegistry;

public class CommonProxy {

  public void preInit(SRegistry registry, FMLPreInitializationEvent event) {

    registry.preInit();

    Config.instance.init(event.getSuggestedConfigurationFile());

    // registry.addRegistrationHandler(new ModPotions(), Potion.class);
    registry.addRegistrationHandler(new ModBlocks(), Block.class);
    registry.addRegistrationHandler(new ModItems(), Item.class);
    registry.addRegistrationHandler(new ModSounds(), SoundEvent.class);
    // ModEntities.init(registry);

    CharacterClass.init();

    // Advancements
    ModTriggers.init(registry);

    // World generators
    GameRegistry.registerWorldGenerator(new WorldGeneratorBB(), 0);

    // NetworkHandler.init();

    // Event handlers registered here
    MinecraftForge.EVENT_BUS.register(new PlayerDataHandler.EventHandler());
    MinecraftForge.EVENT_BUS.register(XPManager.INSTANCE);
    MinecraftForge.EVENT_BUS.register(new CommonEvents());
    MinecraftForge.EVENT_BUS.register(new SkillEvents());
  }

  public void init(SRegistry registry, FMLInitializationEvent event) {

    registry.init();

    for (Skill skill : SkillList.ALL_SKILLS) {
      if (skill instanceof BlockDropsSkill) {
        ((BlockDropsSkill) skill).initDrops();
      }
    }

    Config.instance.save();
  }

  public void postInit(SRegistry registry, FMLPostInitializationEvent event) {

    registry.postInit();
  }

  // public void spawnParticles(EnumModParticles type, Color color, World world, double x, double y,
  // double z, double motionX, double motionY, double motionZ) {
  //
  // }

  public void playSoundOnClient(EntityPlayer player, SoundEvent sound, float volume, float pitch) {

    if (player instanceof EntityPlayerMP) {
      Borderblocks.network.wrapper.sendTo(new MessagePlaySound(sound, volume, pitch), (EntityPlayerMP) player);
    }
  }

  public EntityPlayer getClientPlayer() {

    return null;
  }

  public int getParticleSettings() {

    return 0;
  }
}