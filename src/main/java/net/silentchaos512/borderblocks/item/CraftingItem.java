package net.silentchaos512.borderblocks.item;

import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.init.ModBlocks;
import net.silentchaos512.lib.item.ItemNamedSubtypesSorted;
import net.silentchaos512.lib.registry.RecipeMaker;

public class CraftingItem extends ItemNamedSubtypesSorted {

  public static final String ITEM_NAME = "crafting_material";
  public static final String NAME_ERIDIUM_INGOT = "eridium_ingot";
  public static final String NAME_RELIC_CORE = "relic_core";
  public static final String NAME_RELIC_FRAGMENT = "relic_fragment";
  public static final String NAME_SCRAP = "scrap";
  public static final String NAME_SCRAP_PILE = "scrap_pile";

  public static final String[] NAMES = { NAME_SCRAP, NAME_SCRAP_PILE, NAME_ERIDIUM_INGOT, NAME_RELIC_FRAGMENT, NAME_RELIC_CORE };
  public static final String[] SORTED_NAMES = { NAME_SCRAP, NAME_SCRAP_PILE, NAME_RELIC_FRAGMENT, NAME_RELIC_CORE, NAME_ERIDIUM_INGOT };

  public final ItemStack eridiumIngot = getStack(NAME_ERIDIUM_INGOT);
  public final ItemStack relicCore = getStack(NAME_RELIC_CORE);
  public final ItemStack relicFragment = getStack(NAME_RELIC_FRAGMENT);
  public final ItemStack scrap = getStack(NAME_SCRAP);
  public final ItemStack scrapPile = getStack(NAME_SCRAP_PILE);

  public CraftingItem() {

    super(NAMES, SORTED_NAMES, Borderblocks.MOD_ID, ITEM_NAME);
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {

    return super.getRarity(stack);
  }

  @Override
  public void addRecipes(RecipeMaker recipes) {

    recipes.addShapelessOre("scrap_x4", getStack(NAME_SCRAP, 4), "cobblestone", "stickWood", "plankWood", Items.FLINT);
    recipes.addShapelessOre("scrap_x32", getStack(NAME_SCRAP, 32), "ingotIron", "stickWood", "stickWood", "plankWood", Items.FLINT);
    recipes.addCompression("scrap_pile", scrap, scrapPile, 9);
    recipes.addSmelting(ModBlocks.eridiumOre, getStack(NAME_ERIDIUM_INGOT), 1.5f);
    recipes.addShaped("relic_core", relicCore, "fff", "f f", "fff", 'f', relicFragment);
  }

  @Override
  public void addOreDict() {

    OreDictionary.registerOre("ingotEridium", eridiumIngot);
  }
}
