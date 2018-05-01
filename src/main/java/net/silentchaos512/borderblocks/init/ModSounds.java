package net.silentchaos512.borderblocks.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

public class ModSounds implements IRegistrationHandler<SoundEvent> {

  public static final SoundEvent LEVEL_UP = create("level_up");

  @Override
  public void registerAll(SRegistry reg) {

    reg.registerSoundEvent(LEVEL_UP, "level_up");
  }

  private static SoundEvent create(String soundId) {

    ResourceLocation name = new ResourceLocation(Borderblocks.MOD_ID, soundId);
    return new SoundEvent(name);
  }
}
