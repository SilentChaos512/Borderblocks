package net.silentchaos512.borderblocks.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.init.ModItems;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.SkillConst;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.PlayerHelper;
import net.silentchaos512.lib.util.StackHelper;

public class ScavMultiTool extends ItemPickaxe implements IRegistryObject {

  public static final String NAME = "scav_multi_tool";
  public static final ToolMaterial FAKE_MATERIAL = EnumHelper.addToolMaterial(Borderblocks.RESOURCE_PREFIX + NAME + "_fake", 1, 100, 10f, 5f, 0);

  // Stats
  public static final float[] BREAK_SPEEDS = { 4f, 8f, 16f, 32f, 64f };
  public static final float[] ATTACK_DAMAGE = { 3f, 6f, 11f, 14f, 19f };

  static final String NBT_ID = "id";
  static final String NBT_MAX_TIMEOUT = "max_timeout";

  /** Stores the amount of time remaining for each active multi-tool. */
  final Map<Long, Integer> timeoutMap = new HashMap<>();

  public ScavMultiTool() {

    super(FAKE_MATERIAL);
    this.attackSpeed = -2.0f;
    this.maxStackSize = 1;
    this.setMaxDamage(0);
    this.hasSubtypes = true;
    this.setUnlocalizedName(Borderblocks.RESOURCE_PREFIX + NAME);
  }

  public ItemStack create(EntityPlayer player, ProgressionTier tier, int timeout) {

    ItemStack result = new ItemStack(this, 1, tier.ordinal());
    NBTTagCompound tags = StackHelper.getTagCompound(result, true);

    PlayerData data = PlayerDataHandler.get(player);
    int fortuneLevel = data.getPointsInSkill(SkillList.MULTI_TOOL_FORTUNE);
    int silkLevel = data.getPointsInSkill(SkillList.MULTI_TOOL_SILKTOUCH);
    if (fortuneLevel > 0)
      result.addEnchantment(Enchantments.FORTUNE, fortuneLevel);
    else if (silkLevel > 0)
      result.addEnchantment(Enchantments.SILK_TOUCH, silkLevel);

    // Create an ID for the timeout map, because we can't constantly modify the NBT of harvest
    // tools. Break progress resets when NBT is modified. Could use UUID, but I don't think that's
    // really necessary in this case.
    long id = player.ticksExisted << 32L + player.hashCode();
    tags.setLong(NBT_ID, id);
    tags.setInteger(NBT_MAX_TIMEOUT, timeout);

    timeoutMap.put(id, timeout);
    return result;
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

    if (!(entityIn instanceof EntityPlayer) || entityIn.world.isRemote)
      return;
    EntityPlayer player = (EntityPlayer) entityIn;

    NBTTagCompound tags = StackHelper.getTagCompound(stack, true);
    long id = tags.getLong(NBT_ID);

    if (!timeoutMap.containsKey(id)) {
      // timeoutMap does not contain a value for this multi-tool? Maybe the player restarted
      // their game/server. All we can do is reset the time to the max, I guess?
      timeoutMap.put(id, tags.getInteger(NBT_MAX_TIMEOUT));
    }

    int timeout = timeoutMap.get(id);
    --timeout;

    if (timeout <= 0) {
      // Time ran out, remove the multi-tool
      player.renderBrokenItemStack(stack);
      StackHelper.setCount(stack, 0);
      player.inventory.removeStackFromSlot(itemSlot);
      timeoutMap.remove(id);
    } else {
      timeoutMap.put(id, timeout);
    }
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

    PlayerData data = PlayerDataHandler.get(playerIn);
    int repairLevel = data.getPointsInSkill(SkillList.MULTI_TOOL_REPAIR);

    if (repairLevel > 0) {
      ItemStack tool = playerIn.getHeldItem(handIn);

      // Find scrap
      ItemStack scrap = PlayerHelper.getFirstValidStack(playerIn, true, true, false,
          s -> s.isItemEqual(ModItems.craftingItem.scrap));
      if (StackHelper.isEmpty(scrap)) {
        String line = Borderblocks.localization.getLocalizedString("skill", "multi_tool_repair.noScrap");
        ChatHelper.sendStatusMessage(playerIn, line, true);
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, tool);
      }

      // Find something to repair
      ItemStack toRepair = PlayerHelper.getFirstValidStack(playerIn, true, false, false,
          s -> s.getItem().isRepairable() && s.isItemDamaged());
      if (StackHelper.isValid(toRepair)) {
        if (!worldIn.isRemote) {
          toRepair.setItemDamage(toRepair.getItemDamage() - SkillConst.MULTI_TOOL_REPAIR_AMOUNT);
          scrap.shrink(1);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, tool);
      }
    }

    return super.onItemRightClick(worldIn, playerIn, handIn);
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {

    return true;
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {

    return true;
  }

  @Override
  public int getItemEnchantability() {

    return 0;
  }

  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {

    return false;
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {

    return true;
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {

    NBTTagCompound tags = StackHelper.getTagCompound(stack, true);
    long id = tags.getLong(NBT_ID);
    if (!timeoutMap.containsKey(id))
      return 0.0;

    int current = timeoutMap.get(id);
    int max = tags.getInteger(NBT_MAX_TIMEOUT);
    return 1.0 - (double) current / (double) max;
  }

  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {

    Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

    if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
      int tier = MathHelper.clamp(stack.getItemDamage(), 0, ProgressionTier.values().length - 1);
      float toolAttackDamage = ATTACK_DAMAGE[tier];

      multimap.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
          new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", toolAttackDamage, 0));
    }

    return multimap;
  }

  public Set<String> getToolClasses(ItemStack stack) {

    return ImmutableSet.of("pickaxe", "shovel", "axe");
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {

    int level = super.getHarvestLevel(stack, toolClass, player, blockState);
    return level >= 0 ? getTier(stack).ordinal() : level;
  }

  @Override
  public boolean canHarvestBlock(IBlockState state, ItemStack stack) {

    int toolLevel = getTier(stack).ordinal();
    if (state.getBlock().getHarvestLevel(state) > toolLevel)
      return false;
    return true;
  }

  @Override
  public float getDestroySpeed(ItemStack stack, IBlockState state) {

    float destroySpeed = super.getDestroySpeed(stack, state);
    if (destroySpeed > 1f)
      return BREAK_SPEEDS[getTier(stack).ordinal()];
    return destroySpeed;
  }

  public ProgressionTier getTier(ItemStack stack) {

    return ProgressionTier.byOrdinal(stack.getItemDamage());
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {

    if (!isInCreativeTab(tab))
      return;

    for (ProgressionTier tier : ProgressionTier.values()) {
      ItemStack stack = new ItemStack(this, 1, tier.ordinal());
      NBTTagCompound tags = StackHelper.getTagCompound(stack, true);
      tags.setLong(NBT_ID, tier.ordinal());
      tags.setInteger(NBT_MAX_TIMEOUT, 1200);
      list.add(stack);
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

    int tier = MathHelper.clamp(stack.getItemDamage(), 0, ProgressionTier.values().length - 1);
    tooltip.add(String.format("Harvest speed: %d", (int) BREAK_SPEEDS[tier]));
    tooltip.add(String.format("Harvest level: %d", tier));
  }

  @Override
  public void addRecipes(RecipeMaker recipes) {

  }

  @Override
  public void addOreDict() {

  }

  @Override
  public String getModId() {

    return Borderblocks.MOD_ID;
  }

  @Override
  public String getName() {

    return NAME;
  }

  @Override
  public void getModels(Map<Integer, ModelResourceLocation> models) {

    for (ProgressionTier tier : ProgressionTier.values()) {
      String name = Borderblocks.RESOURCE_PREFIX + "multi_tool_" + tier.name().toLowerCase();
      models.put(tier.ordinal(), new ModelResourceLocation(name, "inventory"));
    }
  }
}
