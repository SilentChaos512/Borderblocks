package net.silentchaos512.borderblocks.init;

import net.minecraft.item.Item;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.item.CraftingItem;
import net.silentchaos512.borderblocks.item.ProgressionRelic;
import net.silentchaos512.borderblocks.item.ScavMultiTool;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

public class ModItems implements IRegistrationHandler<Item> {

  public static ProgressionRelic progressionRelic = new ProgressionRelic();
  public static CraftingItem craftingItem = new CraftingItem();
  public static ScavMultiTool scavMultiTool = new ScavMultiTool();

  @Override
  public void registerAll(SRegistry reg) {

    reg.registerItem(progressionRelic).setCreativeTab(Borderblocks.CREATIVE_TAB);
    reg.registerItem(craftingItem).setCreativeTab(Borderblocks.CREATIVE_TAB);
    reg.registerItem(scavMultiTool).setCreativeTab(Borderblocks.CREATIVE_TAB);
  }
}
