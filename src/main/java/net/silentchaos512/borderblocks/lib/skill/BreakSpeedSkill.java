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

package net.silentchaos512.borderblocks.lib.skill;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.lib.util.StackHelper;

import java.util.ArrayList;
import java.util.List;

public class BreakSpeedSkill extends Skill {
    public static final List<BreakSpeedSkill> LIST_ALL = new ArrayList<>();

    private float amountPerPoint;
    private boolean addInsteadOfMul;

    public BreakSpeedSkill(String name, int maxPoints, float amountPerPoint, boolean addInsteadOfMul) {
        super(name, maxPoints);
        this.amountPerPoint = amountPerPoint;
        this.addInsteadOfMul = addInsteadOfMul;

        LIST_ALL.add(this);
    }

    /**
     * Handle break speed changes, if appropriate. If this skill does not apply to the block being
     * broken, this should do nothing.
     *
     * @param event          The event caught by SkillEvents#onBreakSpeed
     * @param investedPoints The points the player has invested in the skill.
     * @return True if any change was made to the break speed, false otherwise.
     */
    public boolean processBreakSpeed(BreakSpeed event, int investedPoints) {
        IBlockState state = event.getState();
        Block block = state.getBlock();
        int blockMeta = state.getBlock().getMetaFromState(state);
        ItemStack blockStack = new ItemStack(block, 1, blockMeta);

        if (StackHelper.isEmpty(blockStack))
            return false;

        boolean isLog = false;
        boolean isOre = false;

        for (int oreId : OreDictionary.getOreIDs(blockStack)) {
            String oreName = OreDictionary.getOreName(oreId);
            isLog |= oreName.startsWith("log");
            isOre |= oreName.startsWith("ore");
        }

        if ((this == SkillList.BREAK_SPEED_GENERIC && !isLog && !isOre)
                || (this == SkillList.BREAK_SPEED_LOGS && isLog)
                || (this == SkillList.BREAK_SPEED_ORES && isOre)) {
            float increase = amountPerPoint * investedPoints;
            if (addInsteadOfMul)
                event.setNewSpeed(event.getNewSpeed() + increase);
            else
                event.setNewSpeed(event.getNewSpeed() * (1f + increase));
            return true;
        }

        return false;
    }

    @Override
    protected String getDesc2(int investedPoints) {
        return TextFormatting.AQUA + Borderblocks.localization.getLocalizedString("skill", "break_speed_all.desc2", getDesc2Params(investedPoints));
    }

    @Override
    protected Object[] getDesc2Params(int investedPoints) {
        if (addInsteadOfMul)
            return new Object[]{String.format("%.1f", amountPerPoint * investedPoints)};
        else
            return new Object[]{(int) (100 * amountPerPoint * investedPoints) + "%"};
    }
}
