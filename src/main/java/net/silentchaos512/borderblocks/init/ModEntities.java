package net.silentchaos512.borderblocks.init;

import net.silentchaos512.borderblocks.entity.PhantomHorse;
import net.silentchaos512.lib.registry.SRegistry;

public class ModEntities {

  public static void init(SRegistry reg) {

    reg.registerEntity(PhantomHorse.class, "PhantomHorse", 64, 4, true);
  }
}
