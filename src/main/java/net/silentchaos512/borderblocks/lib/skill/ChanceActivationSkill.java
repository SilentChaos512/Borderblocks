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

public class ChanceActivationSkill extends Skill {
    @Getter(value = AccessLevel.PUBLIC)
    float chancePerPoint;

    public ChanceActivationSkill(String name, int maxPoints, float chancePerPoint) throws IllegalArgumentException {
        super(name, maxPoints);
        this.chancePerPoint = chancePerPoint;
    }

    protected Object[] getDesc2Params(int investedPoints) {
        return new Object[]{(int) (100 * chancePerPoint * investedPoints)};
    }
}
