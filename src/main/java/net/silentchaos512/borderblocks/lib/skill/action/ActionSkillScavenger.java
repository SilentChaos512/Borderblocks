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

package net.silentchaos512.borderblocks.lib.skill.action;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.item.ScavMultiTool;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.*;

import java.util.ArrayList;
import java.util.List;

public class ActionSkillScavenger extends ActionSkill {

    public ActionSkillScavenger(ResourceLocation name) {
        super(name);
    }

    @Override
    public boolean activate(EntityPlayer player, ProgressionTier tier, BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown) {
        if (!player.getHeldItemMainhand().isEmpty()) {
            ChatHelper.sendStatusMessage(player, Borderblocks.i18n.translate("skill." + name + ".needEmptyHand"), true);
            return false;
        }

        PlayerData data = PlayerDataHandler.get(player);
        int duration = TimeHelper.ticksFromSeconds(getSkillDuration(data));
        ItemStack stack = ScavMultiTool.create(player, tier, duration);
        PlayerHelper.giveItem(player, stack);

        return true;
    }

    @Override
    public float getCooldownTime() {
        // TODO: Add config?
        return 120f;
    }

    @Override
    public float getSkillDuration(PlayerData playerData) {
        int points = playerData.getPointsInSkill(SkillList.DURATION_BOOST_SCAV);
        return 60f + points * SkillList.DURATION_BOOST_SCAV.getIncreasePerPoint();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getTooltip(int investedPoints, PlayerData playerData) {
        ProgressionTier tier = playerData.getProgressionTier();
        List<String> list = new ArrayList<>();
        list.add(TextFormatting.GOLD + Borderblocks.i18n.translate("skill." + name + ".name"));
        list.add("");
        list.add(Borderblocks.i18n.translate("skill." + name + ".desc1"));
        list.add("");
        list.add(Borderblocks.i18n.translate("skill." + name + ".desc2",
                (int) getSkillDuration(playerData), (int) getCooldownTime(), tier.ordinal()));
        list.add("");
        list.add(Borderblocks.i18n.translate("skill", "action.progressionTier", tier.getFormattedName()));
        list.add(String.format("Points: %d / %d", investedPoints, maxPoints));
        return list;
    }
}
