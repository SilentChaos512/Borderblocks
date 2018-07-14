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

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.ArrayList;
import java.util.List;

public class DamageReductionSkill extends Skill {
    public static final List<DamageReductionSkill> LIST_ALL = new ArrayList<>();
    public static final int FALL_PROTECTION_NEGATE_DISTANCE = 4;

    private DamageSource[] affectedSources;
    private float reducedAmount;

    public DamageReductionSkill(String name, int maxPoints, float reducedAmount, DamageSource... affectedSources) {
        super(name, maxPoints);
        this.affectedSources = affectedSources;
        this.reducedAmount = reducedAmount;

        LIST_ALL.add(this);
    }

    /**
     * Handle damage reduction, if appropriate. Does nothing if this skill does not protect from the
     * given damage source.
     *
     * @param event          The LivingHurtEvent caught in SkillEvents#onPlayerDamage.
     * @param investedPoints The points the player has invested in the skill.
     * @return True if any damage reduction was performed, false otherwise.
     */
    public boolean processDamage(LivingHurtEvent event, int investedPoints) {
        for (DamageSource affectedSource : affectedSources) {
            if (affectedSource != null && affectedSource.damageType.equals(event.getSource().damageType)) {
                float newAmount = event.getAmount() * (1f - reducedAmount * investedPoints);
                event.setAmount(newAmount < 0f ? 0f : newAmount);
                return true;
            }
        }

        return false;
    }

    @Override
    protected Object[] getDesc2Params(int investedPoints) {
        int amount = (int) (100 * reducedAmount * investedPoints);
        if (this == SkillList.FALL_DAMAGE_DOWN) {
            int negateDistance = FALL_PROTECTION_NEGATE_DISTANCE * investedPoints;
            return new Object[]{amount, negateDistance};
        } else if (this == SkillList.FALL_DAMAGE_DOWN_SCAV) {
            int negateDistance = (int) (reducedAmount * investedPoints);
            return new Object[]{negateDistance};
        } else {
            return new Object[]{amount};
        }
    }
}
