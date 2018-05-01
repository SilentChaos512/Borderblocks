package net.silentchaos512.borderblocks.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.StackHelper;

public class ScavMultiTool extends ItemTool implements IRegistryObject {

  public static final String NAME = "scav_multi_tool";
  public static final ToolMaterial FAKE_MATERIAL = EnumHelper.addToolMaterial(Borderblocks.RESOURCE_PREFIX + NAME + "_fake", 1, 100, 10f, 5f, 0);
  public static final float[] BREAK_SPEEDS = new float[] { 4f, 6f, 8f, 12f, 16f };

  static final String NBT_ID = "id";
  static final String NBT_MAX_TIMEOUT = "max_timeout";

  /** Stores the amount of time remaining for each active multi-tool. */
  final Map<Long, Integer> timeoutMap = new HashMap<>();

  public ScavMultiTool() {

    super(FAKE_MATERIAL, ImmutableSet.of());
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
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {

    return true;
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
    // if (!worldIn.isRemote && (double)state.getBlockHardness(worldIn, pos) != 0.0D)
    // {
    // stack.damageItem(1, entityLiving);
    // }

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
  public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {

    Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

    if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double) this.attackDamage, 0));
      multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) this.attackSpeed, 0));
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
