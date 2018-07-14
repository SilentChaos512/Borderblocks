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
import net.silentchaos512.borderblocks.lib.character.CharacterClass;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UseActionSkillTrigger implements ICriterionTrigger<UseActionSkillTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(Borderblocks.MOD_ID, "use_action_skill");
    private final Map<PlayerAdvancements, UseActionSkillTrigger.Listeners> listeners = Maps.<PlayerAdvancements, UseActionSkillTrigger.Listeners>newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UseActionSkillTrigger.Instance> listener) {
        UseActionSkillTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (consumeitemtrigger$listeners == null) {
            consumeitemtrigger$listeners = new UseActionSkillTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
        }

        consumeitemtrigger$listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UseActionSkillTrigger.Instance> listener) {
        UseActionSkillTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

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
    public UseActionSkillTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        String className = JsonUtils.getString(json, "class_name");
        return new UseActionSkillTrigger.Instance(className);
    }

    public static class Instance extends AbstractCriterionInstance {
        String className;

        public Instance(String className) {
            super(UseActionSkillTrigger.ID);
            this.className = className;
        }

        public boolean test(PlayerData data) {
            CharacterClass targetClass = CharacterClass.getByName(className);
            if (targetClass != CharacterClass.CLASS_UNDEFINED)
                return data.getCharacterClass() == targetClass;
            else
                return data.getCharacterClass() != CharacterClass.CLASS_UNDEFINED;
        }
    }

    public void trigger(EntityPlayerMP player) {
        UseActionSkillTrigger.Listeners triggerListeners = this.listeners.get(player.getAdvancements());

        if (triggerListeners != null) {
            PlayerData data = PlayerDataHandler.get(player);
            triggerListeners.trigger(data);
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<UseActionSkillTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<UseActionSkillTrigger.Instance>>newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<UseActionSkillTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<UseActionSkillTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(PlayerData data) {
            List<ICriterionTrigger.Listener<UseActionSkillTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<UseActionSkillTrigger.Instance> listener : this.listeners) {
                if (((UseActionSkillTrigger.Instance) listener.getCriterionInstance()).test(data)) {
                    if (list == null) {
                        list = Lists.<ICriterionTrigger.Listener<UseActionSkillTrigger.Instance>>newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<UseActionSkillTrigger.Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
