/*
 * Borderblocks
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.borderblocks.item;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.SkillConst;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.PlayerHelper;

import javax.annotation.Nullable;
import java.util.*;

public class ScavMultiTool extends ItemPickaxe {
    private static final ToolMaterial FAKE_MATERIAL = EnumHelper.addToolMaterial(Borderblocks.RESOURCE_PREFIX + "scav_multi_tool_fake", 1, 100, 10f, 5f, 0);

    // Stats
    private static final float[] BREAK_SPEEDS = {4f, 8f, 16f, 32f, 64f};
    private static final float[] ATTACK_DAMAGE = {3f, 6f, 11f, 14f, 19f};

    private static final String NBT_ID = "id";
    private static final String NBT_MAX_TIMEOUT = "max_timeout";

    /**
     * Stores the amount of time remaining for each active multi-tool.
     */
    private static final Map<Long, Integer> TIMEOUT_MAP = new HashMap<>();
    private static final Map<ProgressionTier, ScavMultiTool> ITEMS = new HashMap<>();
    private static final String NBT_ROOT = "ScavMultiTool";

    private final ProgressionTier tier;

    public ScavMultiTool(ProgressionTier tier) {
        super(FAKE_MATERIAL);
        this.tier = tier;
        this.attackSpeed = -2.0f;
        this.maxStackSize = 1;
        this.setMaxDamage(0);
        this.setHarvestLevel("pickaxe", 0);
        this.setHarvestLevel("shovel", 0);
        this.setHarvestLevel("axe", 0);
        ITEMS.put(tier, this);
    }

    public static ItemStack create(EntityPlayer player, ProgressionTier tier, int timeout) {
        ScavMultiTool item = Objects.requireNonNull(ITEMS.get(tier));
        ItemStack result = new ItemStack(item, 1);
        NBTTagCompound tags = result.getOrCreateSubCompound(NBT_ROOT);

        PlayerData data = PlayerDataHandler.get(player);
        if (data != null) {
            int fortuneLevel = data.getPointsInSkill(SkillList.MULTI_TOOL_FORTUNE);
            int silkLevel = data.getPointsInSkill(SkillList.MULTI_TOOL_SILKTOUCH);
            if (fortuneLevel > 0)
                result.addEnchantment(Enchantments.FORTUNE, fortuneLevel);
            else if (silkLevel > 0)
                result.addEnchantment(Enchantments.SILK_TOUCH, silkLevel);
        }

        // Create an ID for the timeout map, because we can't constantly modify the NBT of harvest
        // tools. Break progress resets when NBT is modified. Could use UUID, but I don't think that's
        // really necessary in this case.
        long id = player.ticksExisted << 32L + player.hashCode();
        tags.setLong(NBT_ID, id);
        tags.setInteger(NBT_MAX_TIMEOUT, timeout);

        TIMEOUT_MAP.put(id, timeout);
        return result;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!(entityIn instanceof EntityPlayer) || entityIn.world.isRemote)
            return;
        EntityPlayer player = (EntityPlayer) entityIn;

        NBTTagCompound tags = stack.getOrCreateSubCompound("ScavMultiTool");
        long id = tags.getLong(NBT_ID);

        if (!TIMEOUT_MAP.containsKey(id)) {
            // timeoutMap does not contain a value for this multi-tool? Maybe the player restarted
            // their game/server. All we can do is reset the time to the max, I guess?
            TIMEOUT_MAP.put(id, tags.getInteger(NBT_MAX_TIMEOUT));
        }

        int timeout = TIMEOUT_MAP.get(id);
        --timeout;

        if (timeout <= 0) {
            // Time ran out, remove the multi-tool
            player.renderBrokenItemStack(stack);
            stack.setCount(0);
            player.inventory.removeStackFromSlot(itemSlot);
            TIMEOUT_MAP.remove(id);
        } else {
            TIMEOUT_MAP.put(id, timeout);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        PlayerData data = PlayerDataHandler.get(playerIn);
        int repairLevel = data != null ? data.getPointsInSkill(SkillList.MULTI_TOOL_REPAIR) : 0;

        if (repairLevel > 0) {
            ItemStack tool = playerIn.getHeldItem(handIn);

            // Find scrap
            ItemStack scrap = PlayerHelper.getFirstValidStack(playerIn, true, true, false,
                    s -> s.getItem() == CraftingItems.SCRAP.getItem());
            if (scrap.isEmpty()) {
                String line = Borderblocks.i18n.translate("skill", "multi_tool_repair.noScrap");
                ChatHelper.sendStatusMessage(playerIn, line, true);
                return new ActionResult<>(EnumActionResult.FAIL, tool);
            }

            // Find something to repair
            ItemStack toRepair = PlayerHelper.getFirstValidStack(playerIn, true, false, false,
                    s -> s.getItem().isRepairable() && s.isItemDamaged());
            if (!toRepair.isEmpty()) {
                if (!worldIn.isRemote) {
                    toRepair.setItemDamage(toRepair.getItemDamage() - SkillConst.MULTI_TOOL_REPAIR_AMOUNT);
                    scrap.shrink(1);
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, tool);
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
        NBTTagCompound tags = stack.getOrCreateSubCompound("ScavMultiTool");
        long id = tags.getLong(NBT_ID);
        if (!TIMEOUT_MAP.containsKey(id))
            return 0.0;

        int current = TIMEOUT_MAP.get(id);
        int max = tags.getInteger(NBT_MAX_TIMEOUT);
        return 1.0 - (double) current / (double) max;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            float toolAttackDamage = ATTACK_DAMAGE[this.tier.ordinal()];

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
        return level >= 0 ? this.tier.ordinal() : level;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        int toolLevel = this.tier.ordinal();
        return state.getBlock().getHarvestLevel(state) <= toolLevel;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        float destroySpeed = super.getDestroySpeed(stack, state);
        if (destroySpeed > 1f)
            return BREAK_SPEEDS[this.tier.ordinal()];
        return destroySpeed;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (!isInCreativeTab(tab))
            return;

        ItemStack stack = new ItemStack(this);
        NBTTagCompound tags = stack.getOrCreateSubCompound(NBT_ROOT);
        tags.setLong(NBT_ID, this.tier.ordinal());
        tags.setInteger(NBT_MAX_TIMEOUT, 1200);
        list.add(stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(String.format("Harvest speed: %d", (int) BREAK_SPEEDS[this.tier.ordinal()]));
        tooltip.add(String.format("Harvest level: %d", this.tier.ordinal()));
    }
}
