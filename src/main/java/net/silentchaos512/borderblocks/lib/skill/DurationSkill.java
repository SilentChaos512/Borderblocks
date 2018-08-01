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
import net.minecraft.util.ResourceLocation;

public class DurationSkill extends Skill {
    @Getter(value = AccessLevel.PUBLIC)
    float increasePerPoint;

    public DurationSkill(ResourceLocation name, int maxPoints, float increasePerPoint) {
        super(name, maxPoints);
        this.increasePerPoint = increasePerPoint;
    }

    @Override
    protected Object[] getDesc2Params(int investedPoints) {
        return new Object[]{(int) (increasePerPoint * investedPoints)};
    }
}
