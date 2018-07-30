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

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.advancements.ModTriggers;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.lib.item.IColoredItem;
import net.silentchaos512.lib.util.ChatHelper;

import java.awt.*;
import java.util.List;

public class ProgressionRelic extends Item implements IColoredItem {
    private final ProgressionTier tier;

    public ProgressionRelic(ProgressionTier tier) {
        this.tier = tier;
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
        if (data == null) return new ActionResult<>(EnumActionResult.PASS, stack);

        if (player instanceof EntityPlayerMP)
            ModTriggers.USE_ITEM.trigger((EntityPlayerMP) player, stack);

        // Tier already achieved?
        if (data.getProgressionTier().ordinal() >= this.tier.ordinal()) {
            if (world.isRemote)
                return new ActionResult<>(EnumActionResult.PASS, stack);
            ChatHelper.translate(player, Borderblocks.i18n.getKey("item", "progression_relic", "cannotUse"));
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (world.isRemote)
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        // Upgrade!
        data.setProgressionTier(this.tier);
        stack.shrink(1);
        ChatHelper.translate(player, Borderblocks.i18n.getKey("item", "progression_relic", "used"));

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public IItemColor getColorHandler() {
        return (stack, tintIndex) -> {
            if (tintIndex == 1) {
                float period = 40f;
                return Color.HSBtoRGB((ClientTicks.ticksInGame % period) / period, 0.6f, 1.0f);
            }
            return 0xFFFFFF;
        };
    }
}
