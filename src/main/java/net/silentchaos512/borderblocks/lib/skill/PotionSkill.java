/*
 * Borderblocks -- PotionEffectSkill
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.borderblocks.Borderblocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PotionSkill extends Skill {
    private final List<Effect> effects = new ArrayList<>();
    private final boolean applyAlways;

    public PotionSkill(ResourceLocation name, int maxPoints, boolean applyAlways, Effect... effectsIn) {
        super(name, maxPoints);
        this.applyAlways = applyAlways;
        Collections.addAll(this.effects, effectsIn);
    }

    @Override
    public void trigger(EntityPlayer player, int pointsInSkill) {
        super.trigger(player, pointsInSkill);
        this.effects.forEach(e -> player.addPotionEffect(e.getEffect(pointsInSkill, applyAlways, !applyAlways)));
    }

    @Override
    public void applyTo(EntityPlayer player, int pointsInSkill) {
        if (applyAlways)
            trigger(player, pointsInSkill);
    }

    @Override
    public void removeFrom(EntityPlayer player) {
        if (applyAlways)
            this.effects.forEach(e -> player.removePotionEffect(e.potion));
    }

    @Override
    protected Object[] getDesc2Params(int investedPoints) {
        List<Object> list = new ArrayList<>();
        for (Effect effect : this.effects) {
            list.add(Borderblocks.i18n.translate(effect.potion.getName()));
            String potency = Borderblocks.i18n.translate("potion.potency." + effect.getAmplifier(investedPoints));
            list.add((potency.isEmpty() ? "" : " ") + potency);
            list.add(effect.getDuration(investedPoints) / 20);
        }
        return list.toArray();
    }

    public static class Effect {
        private final Potion potion;
        private final float durationBase;
        private final float durationPerPoint;
        private final float amplifierBase;
        private final float amplifierPerPoint;

        public Effect(Potion potion, float durationBase, float durationPerPoint) {
            this(potion, durationBase, durationPerPoint, 0, 0);
        }

        public Effect(Potion potion, float durationBase, float durationPerPoint, float amplifierBase, float amplifierPerPoint) {
            this.potion = potion;
            this.durationBase = durationBase;
            this.durationPerPoint = durationPerPoint;
            this.amplifierBase = amplifierBase;
            this.amplifierPerPoint = amplifierPerPoint;
        }

        public PotionEffect getEffect(int pointsInSkill) {
            return new PotionEffect(this.potion, this.getDuration(pointsInSkill), this.getAmplifier(pointsInSkill));
        }

        public PotionEffect getEffect(int pointsInSkill, boolean ambient, boolean showParticles) {
            return new PotionEffect(this.potion, this.getDuration(pointsInSkill), this.getAmplifier(pointsInSkill), ambient, showParticles);
        }

        private int getDuration(int points) {
            return 20 * (int) (this.durationBase + this.durationPerPoint * (points - 1));
        }

        private int getAmplifier(int points) {
            return (int) (this.amplifierBase + this.amplifierPerPoint * (points - 1));
        }
    }
}
