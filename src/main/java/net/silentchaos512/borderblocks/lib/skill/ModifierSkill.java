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

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class ModifierSkill extends Skill {

    private static final double ARMOR_BOOST_SPEED_DECREASE = 0.02;

    private IAttribute affectedAttribute;
    private double amountPerPoint;
    private int operation;
    private UUID uuid;
    private boolean alwaysApply = true;

    public ModifierSkill(ResourceLocation name, int maxPoints, IAttribute attribute, double amountPerPoint, int operation) {
        this(name, maxPoints, attribute, amountPerPoint, operation, true);
    }

    public ModifierSkill(ResourceLocation name, int maxPoints, IAttribute attribute, double amountPerPoint, int operation, boolean alwaysApply) {
        super(name, maxPoints);
        this.affectedAttribute = attribute;
        this.amountPerPoint = amountPerPoint;
        this.operation = operation;
        this.uuid = UUID.nameUUIDFromBytes(name.toString().getBytes());
        this.alwaysApply = alwaysApply;
    }

    @Override
    public void applyTo(EntityPlayer player, int pointsInSkill) {
        IAttributeInstance attr = player.getEntityAttribute(affectedAttribute);
        applyModifier(attr, pointsInSkill, amountPerPoint, operation);

        if (this == SkillList.ARMOR_BOOST) {
            attr = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            applyModifier(attr, pointsInSkill, -ARMOR_BOOST_SPEED_DECREASE, 1);
        }
    }

    @Override
    public void removeFrom(EntityPlayer player) {
        IAttributeInstance attr = player.getEntityAttribute(affectedAttribute);
        removeModifier(attr);

        if (this == SkillList.ARMOR_BOOST) {
            attr = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            removeModifier(attr);
        }
    }

    private void applyModifier(IAttributeInstance attr, int pointsInSkill, double perPoint, int op) {
        if (attr != null) {
            // Get current and new modifier.
            AttributeModifier mod = attr.getModifier(uuid);
            AttributeModifier newMod = new AttributeModifier(uuid, name.toString(), perPoint * pointsInSkill, op);

            // Remove the old, apply the new.
            if (mod != null)
                attr.removeModifier(mod);
            attr.applyModifier(newMod);
        }
    }

    private void removeModifier(IAttributeInstance attr) {
        if (attr != null) {
            AttributeModifier mod = attr.getModifier(uuid);
            if (mod != null)
                attr.removeModifier(mod);
        }
    }

    @Override
    protected Object[] getDesc2Params(int investedPoints) {
        Object[] ret = new Object[isKillSkill ? 2 : 1];

        if (this == SkillList.ARMOR_BOOST) {
            float value1 = (float) (amountPerPoint * investedPoints);
            int value2 = (int) (100 * ARMOR_BOOST_SPEED_DECREASE * investedPoints);
            return new Object[]{value1, value2};
        } else if (operation == 0) {
            ret[0] = (float) (amountPerPoint * investedPoints);
        } else {
            ret[0] = (int) (100 * amountPerPoint * investedPoints);
        }

        if (isKillSkill)
            ret[1] = (int) killSkillDuration;
        return ret;
    }
}
