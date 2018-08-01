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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import javax.annotation.Nullable;

public abstract class ActionSkill extends Skill {

    public ActionSkill(ResourceLocation name) {
        super(name, 1);
    }

    public abstract boolean activate(EntityPlayer player, ProgressionTier tier, @Nullable BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown);

    public abstract float getCooldownTime();

    public abstract float getSkillDuration(PlayerData data);

    public double getSkillReach(PlayerData data) {
        return 16.0;
    }
}
