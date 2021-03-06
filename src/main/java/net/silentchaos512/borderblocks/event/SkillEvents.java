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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.lib.skill.*;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import java.util.Map;
import java.util.Map.Entry;

public class SkillEvents {

    @SubscribeEvent
    public void onPlayerJoinedServer(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            applySkillEffects(event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.player instanceof EntityPlayerMP && event.player.ticksExisted % 20 == 0) {
            applySkillEffects(event.player);
        }
    }

    private void applySkillEffects(EntityPlayer player) {
        PlayerData data = PlayerDataHandler.get(player);
        if (data == null) return;
        data.resetCooldownBonuses();

        // Apply skill modifiers
        for (Skill skill : SkillList.ALL_SKILLS)
            skill.removeFrom(player);
        for (Entry<Skill, Integer> entry : data.getSkills().entrySet()) {
            Skill skill = entry.getKey();
            Integer points = entry.getValue();
            if (!skill.isKillSkill() || data.getActiveKillSkills().containsKey(skill))
                skill.applyTo(player, points);
        }
    }

    @SubscribeEvent
    public void onPlayerTakeDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer))
            return;

        PlayerData data = PlayerDataHandler.get((EntityPlayer) event.getEntity());
        if (data == null) return;

        for (DamageReductionSkill skill : DamageReductionSkill.LIST_ALL) {
            int pointsInSkill = data.getPointsInSkill(skill);
            if (pointsInSkill > 0) {
                skill.processDamage(event, pointsInSkill);
            }
        }

        if (event.getAmount() <= 0f)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer))
            return;

        PlayerData data = PlayerDataHandler.get((EntityPlayer) event.getEntity());
        if (data == null) return;
        int points = Math.max(data.getPointsInSkill(SkillList.FALL_DAMAGE_DOWN), data.getPointsInSkill(SkillList.FALL_DAMAGE_DOWN_SCAV));
        if (points > 0) {
            int negatedDistance = DamageReductionSkill.FALL_PROTECTION_NEGATE_DISTANCE * points;
            if (event.getDistance() <= negatedDistance)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(BreakSpeed event) {
        if (!(event.getEntity() instanceof EntityPlayer))
            return;

        PlayerData data = PlayerDataHandler.get((EntityPlayer) event.getEntity());
        if (data == null) return;

        for (BreakSpeedSkill skill : BreakSpeedSkill.LIST_ALL) {
            int pointsInSkill = data.getPointsInSkill(skill);
            if (pointsInSkill > 0)
                skill.processBreakSpeed(event, pointsInSkill);
        }
    }

    @SubscribeEvent
    public void onBlockDrops(HarvestDropsEvent event) {
        EntityPlayer player = event.getHarvester();
        PlayerData data = PlayerDataHandler.get(player);
        if (data == null) return;

        Map<BlockDropsSkill, Integer> map = data.getSkillsOfType(BlockDropsSkill.class);
        for (Entry<BlockDropsSkill, Integer> entry : map.entrySet())
            entry.getKey().processDrops(event, entry.getValue(), data);
    }

    @SubscribeEvent
    public void onArrowImpact(ProjectileImpactEvent.Arrow event) {
        Entity entityHit = event.getRayTraceResult().entityHit;
        if (!(entityHit instanceof EntityPlayer) || entityHit.world.isRemote)
            return;

        EntityPlayer player = (EntityPlayer) entityHit;
        PlayerData data = PlayerDataHandler.get(player);
        if (data == null) return;
        boolean shooterIsPlayer = event.getArrow() != null && event.getArrow().shootingEntity instanceof EntityPlayer;
        float baseChance = SkillList.ARROW_DEFLECTION.getChancePerPoint();
        int pointsInSkill = data.getPointsInSkill(SkillList.ARROW_DEFLECTION);
        float deflectionChance = (shooterIsPlayer ? baseChance * 2f / 3f : baseChance) * pointsInSkill;

        if (Borderblocks.random.nextFloat() < deflectionChance) {
            event.setCanceled(true);
            Borderblocks.proxy.playSoundOnClient(player, SoundEvents.BLOCK_ANVIL_PLACE, 0.5f, 2.0f);
            event.getArrow().setDead();
        }
    }

    @SubscribeEvent
    public void mobGriefing(EntityMobGriefingEvent event) {
        // Creepers detonated by Siren's ignition skill
        if (event.getEntity() != null && event.getEntity().getEntityData().getBoolean(SkillList.IGNITION.getName().toString()))
            event.setResult(Result.DENY);
    }

    @SubscribeEvent
    public void onMobDeath(LivingDeathEvent event) {
        // Activate player's kill skills
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            PlayerData data = PlayerDataHandler.get(player);
            if (data == null) return;
            for (Skill skill : data.getSkills().keySet())
                if (skill.isKillSkill())
                    data.activateKillSkill(skill);
        }
    }
}
