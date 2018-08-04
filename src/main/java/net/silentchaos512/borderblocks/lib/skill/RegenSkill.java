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

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RegenSkill extends Skill {
    @Getter(value = AccessLevel.PUBLIC)
    float regenRate;

    public RegenSkill(ResourceLocation name, int maxPoints, float regenRate) throws IllegalArgumentException {
        super(name, maxPoints);
        this.regenRate = regenRate;
    }

    @Override
    public void trigger(EntityPlayer player, int investedPoints) {
        player.heal(regenRate * investedPoints * player.getMaxHealth());
    }

    @Override
    protected Object[] getDesc2Params(int investedPoints) {
        return new Object[]{100f * regenRate * investedPoints};
    }
}
