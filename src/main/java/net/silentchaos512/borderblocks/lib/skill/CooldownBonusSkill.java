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

import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public class CooldownBonusSkill extends Skill {

    private float amountPerPoint;

    public CooldownBonusSkill(String name, int maxPoints, float amountPerPoint) {

        super(name, maxPoints);
        this.amountPerPoint = amountPerPoint;
    }

    @Override
    public void applyTo(EntityPlayer player, int pointsInSkill) {

        PlayerData data = PlayerDataHandler.get(player);
        data.addCooldownBonus(amountPerPoint * pointsInSkill);
    }

    @Override
    protected Object[] getDesc2Params(int investedPoints) {

        return new Object[]{(int) (100 * amountPerPoint * investedPoints)};
    }
}
