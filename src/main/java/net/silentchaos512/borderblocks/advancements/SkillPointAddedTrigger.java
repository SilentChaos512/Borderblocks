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

package net.silentchaos512.borderblocks.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillPointAddedTrigger implements ICriterionTrigger<SkillPointAddedTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(Borderblocks.MOD_ID, "skill_point_added");
    private final Map<PlayerAdvancements, SkillPointAddedTrigger.Listeners> listeners = Maps.<PlayerAdvancements, SkillPointAddedTrigger.Listeners>newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance> listener) {
        SkillPointAddedTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (consumeitemtrigger$listeners == null) {
            consumeitemtrigger$listeners = new SkillPointAddedTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
        }

        consumeitemtrigger$listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance> listener) {
        SkillPointAddedTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (consumeitemtrigger$listeners != null) {
            consumeitemtrigger$listeners.remove(listener);

            if (consumeitemtrigger$listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public SkillPointAddedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        String skillName = JsonUtils.getString(json, "skill_name");
        int points = JsonUtils.hasField(json, "points") ? JsonUtils.getInt(json, "points") : 1;
        return new SkillPointAddedTrigger.Instance(skillName, points);
    }

    public static class Instance extends AbstractCriterionInstance {
        String skillName;
        int points = 1;

        public Instance(String skillName, int points) {
            super(SkillPointAddedTrigger.ID);
            this.skillName = skillName;
            this.points = points;
        }

        public boolean test(PlayerData data, Skill skill, int points) {
            Skill skillThis = SkillList.getByName(this.skillName);
            return skill != null && (skillThis == null || skillThis == skill) && data.getPointsInSkill(skill) >= points;
        }
    }

    public void trigger(EntityPlayerMP player, Skill skill, int points) {
        SkillPointAddedTrigger.Listeners triggerListeners = this.listeners.get(player.getAdvancements());

        if (triggerListeners != null) {
            PlayerData data = PlayerDataHandler.get(player);
            triggerListeners.trigger(data, skill, points);
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance>>newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(PlayerData data, Skill skill, int points) {
            List<ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance> listener : this.listeners) {
                if (((SkillPointAddedTrigger.Instance) listener.getCriterionInstance()).test(data, skill, points)) {
                    if (list == null) {
                        list = Lists.<ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance>>newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<SkillPointAddedTrigger.Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
