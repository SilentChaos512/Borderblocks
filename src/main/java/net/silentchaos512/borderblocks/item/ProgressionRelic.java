package net.silentchaos512.borderblocks.item;

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.advancements.ModTriggers;
import net.silentchaos512.borderblocks.init.ModItems;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.item.ItemSL;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.StackHelper;

public class ProgressionRelic extends ItemSL {

  public static final String NAME = "progression_relic";

  public ProgressionRelic() {

    super(ProgressionTier.values().length, Borderblocks.MOD_ID, NAME);
    this.setMaxStackSize(1);
  }

  public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {

    // TODO
    list.add("Used to upgrade action skills (right-click).");
    list.add("Recipes likely to change, but feedback appreciated.");
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

    ItemStack stack = player.getHeldItem(hand);

    PlayerData data = PlayerDataHandler.get(player);
    ProgressionTier tierOfStack = ProgressionTier.byOrdinal(stack.getItemDamage());

    if (player instanceof EntityPlayerMP)
      ModTriggers.USE_ITEM.trigger((EntityPlayerMP) player, stack);

    // Tier already achieved?
    LocalizationHelper loc = Borderblocks.localization;
    if (data.getProgressionTier().ordinal() >= tierOfStack.ordinal()) {
      if (world.isRemote)
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
      String line = loc.getItemSubText(itemName, "cannotUse");
      ChatHelper.sendStatusMessage(player, line, true);
      return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    if (world.isRemote)
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

    // Upgrade!
    data.setProgressionTier(tierOfStack);
    StackHelper.shrink(stack, 1);
    String tierText = loc.getMiscText("progressionTier." + tierOfStack.name().toLowerCase());
    String line = loc.getItemSubText(NAME, "used", tierText);
    ChatHelper.sendMessage(player, line);

    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
  }

  @Override
  public String getNameForStack(ItemStack stack) {

    return itemName + "_" + ProgressionTier.byOrdinal(stack.getItemDamage()).name().toLowerCase();
  }

  @Override
  public void getModels(Map<Integer, ModelResourceLocation> models) {

    for (ProgressionTier tier : ProgressionTier.values()) {
      String name = getFullName() + "_" + tier.name().toLowerCase();
      models.put(tier.ordinal(), new ModelResourceLocation(name, "inventory"));
    }
  }

  @Override
  public EnumRarity getRarity(ItemStack stack) {

    return EnumRarity.EPIC;
  }

  @Override
  public void addRecipes(RecipeMaker recipes) {

    ItemStack relicWood = new ItemStack(this, 1, ProgressionTier.WOOD.ordinal());
    ItemStack relicStone = new ItemStack(this, 1, ProgressionTier.STONE.ordinal());
    ItemStack relicIron = new ItemStack(this, 1, ProgressionTier.IRON.ordinal());
    ItemStack relicDiamond = new ItemStack(this, 1, ProgressionTier.DIAMOND.ordinal());
    ItemStack relicEridium = new ItemStack(this, 1, ProgressionTier.ERIDIUM.ordinal());

    recipes.addShapedOre("progression_relic_wood", relicWood,
        "mmm", "mim", "mmm",
        'm', "logWood", 'i', ModItems.craftingItem.scrapPile);
    recipes.addShapedOre("progression_relic_stone", relicStone,
        "mim", "mcm", "mim",
        'm', "stone", 'i', ModItems.craftingItem.scrapPile, 'c', relicWood);
    recipes.addShapedOre("progression_relic_iron", relicIron,
        "sis", "mcm", "sis",
        's', "ingotIron", 'm', "blockIron", 'i', ModItems.craftingItem.scrapPile, 'c', relicStone);
    recipes.addShapedOre("progression_relic_diamond", relicDiamond,
        "sis", "mcm", "sis",
        's', "gemDiamond", 'm', "blockDiamond", 'i', ModItems.craftingItem.scrapPile, 'c', relicIron);
    recipes.addShapedOre("progression_relic_eridium", relicEridium,
        "sis", "mcm", "sis",
        's', "ingotEridium", 'm', "blockEridium", 'i', ModItems.craftingItem.scrapPile, 'c', relicDiamond);
  }
}