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

package net.silentchaos512.borderblocks.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import java.util.LinkedList;
import java.util.Queue;

public class PetEvents {
    /**
     * Entities in the queue will be healed to max health at the end of the server tick.
     */
    private Queue<EntityLivingBase> healQueue = new LinkedList<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getChild() instanceof EntityTameable))
            return;

        EntityPlayer player = event.getCausedByPlayer();
        if (player == null)
            return;

        PlayerData data = PlayerDataHandler.get(player);
        if (data == null)
            return;

        applyTamedAnimalModifiers(data, event.getChild());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAnimalTamed(AnimalTameEvent event) {
        if (event.getAnimal() == null || event.getTamer() == null)
            return;

        PlayerData data = PlayerDataHandler.get(event.getTamer());
        if (data == null)
            return;

        applyTamedAnimalModifiers(data, event.getAnimal());
    }

    private void applyTamedAnimalModifiers(PlayerData data, EntityLivingBase animal) {
        int healthBoostLevel = data.getPointsInSkill(SkillList.PET_HEALTH_UP);
        if (healthBoostLevel > 0) {
            float healthIncrease = healthBoostLevel * SkillList.PET_HEALTH_UP.getModifierAmount();
            AttributeModifier mod = new AttributeModifier("pet_health_boost", healthIncrease, 1);
            animal.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(mod);
            // Queue entity to be healed later... because wolves >:(
            healQueue.add(animal);
        }
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent event) {
        if (event.phase == Phase.END) {
            while (!healQueue.isEmpty()) {
                EntityLivingBase entity = healQueue.poll();
                if (entity != null) entity.setHealth(entity.getMaxHealth());
            }
        }
    }
}
