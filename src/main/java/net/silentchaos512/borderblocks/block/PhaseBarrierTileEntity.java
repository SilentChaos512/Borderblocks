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

package net.silentchaos512.borderblocks.block;

import lombok.AccessLevel;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.borderblocks.util.XPManager;

public class PhaseBarrierTileEntity extends TileEntity implements ITickable {
    @Setter(value = AccessLevel.PUBLIC)
    BlockPos centerPos;
    @Setter(value = AccessLevel.PUBLIC)
    int radius;
    @Setter(value = AccessLevel.PUBLIC)
    int timeRemaining = 100;
    @Setter(value = AccessLevel.PUBLIC)
    boolean primaryCore = false;

    private EntityPlayer owner = null;
    private int suffocateLevel = 0;
    private int ignitionLevel = 0;
    private int chillLevel = 0;

    @Override
    public void update() {
        if (world.isRemote)
            return;

        if (--timeRemaining <= 0)
            SkillList.ACTION_SIREN.removeBarrier(getWorld(), centerPos, radius);

        // Skill effects (primary core only!)
        if (!primaryCore)
            return;

        if (timeRemaining % 20 == 0 && owner != null) {
            for (EntityLivingBase mob : world.getEntities(EntityLivingBase.class, this::isHostileMobInBarrier)) {
                // Suffocate
                if (suffocateLevel > 0) {
                    mob.attackEntityFrom(DamageSource.DROWN, 1.2f * suffocateLevel);
                    if (mob.getHealth() <= 0f)
                        XPManager.INSTANCE.awardXpForKill(mob, owner, true);
                }

                // Chill
                if (chillLevel > 0) {
                    SkillList.BARRIER_SLOW_DOWN.trigger(mob, chillLevel);
                }

                // Ignition
                if (ignitionLevel > 0 && mob instanceof EntityCreeper) {
                    ((EntityCreeper) mob).ignite();
                    // SkillEvents#mobGriefing will prevent any mob with this tag from damaging terrain.
                    mob.getEntityData().setBoolean(SkillList.IGNITION.getName().toString(), true);
                }
            }

            if (owner != null) {
                PlayerData data = PlayerDataHandler.get(owner);
                if (data == null) return;
                // Regen
                int regenLevel = data.getPointsInSkill(SkillList.REGEN_SIREN);
                if (regenLevel > 0) {
                    SkillList.REGEN_SIREN.trigger(owner, regenLevel);
                }

                // Player-in-barrier skills
                int speedBooster = data.getPointsInSkill(SkillList.BARRIER_SPEED_BOOST);
                int resistBoost = data.getPointsInSkill(SkillList.BARRIER_RESIST_BOOST);
                int oxygen = data.getPointsInSkill(SkillList.BARRIER_OXYGEN_BUBBLE);
                for (EntityPlayer player : world.getPlayers(EntityPlayer.class, this::isFriendlyPlayerInBarrier)) {
                    if (speedBooster > 0)
                        SkillList.BARRIER_SPEED_BOOST.trigger(player, speedBooster);
                    if (resistBoost > 0)
                        SkillList.BARRIER_RESIST_BOOST.trigger(player, resistBoost);
                    if (oxygen > 0 && player.getAir() < 300) { // Vanilla max air (see EntityLivingBase#onEntityUpdate
                        player.setAir(player.getAir() + 5);
                        player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 40, 0, true, false));
                    }
                }
            }
        }
    }

    public void setSkillProperties(PlayerData data) {
        this.owner = data.playerWR.get();
        this.suffocateLevel = data.getPointsInSkill(SkillList.BARRIER_SUFFOCATE);
        this.ignitionLevel = data.getPointsInSkill(SkillList.IGNITION);
        this.chillLevel = data.getPointsInSkill(SkillList.BARRIER_SLOW_DOWN);
    }

    public int getRadiusSq() {
        // Just radius^2 does not get entities standing inside the barrier blocks.
        // What I really want to do is to add 0.5 to radius, but this is basically the same.
        // Actual value is just 0.25 more.
        return radius * (radius + 1);
    }

    private boolean isHostileMobInBarrier(EntityLivingBase entity) {
        return entity instanceof IMob && entity.getDistanceSq(this.centerPos) <= getRadiusSq();
    }

    private boolean isFriendlyPlayerInBarrier(EntityPlayer player) {
        // TODO: Consider teams? Just assuming a PvE environment right now...
        return player.getDistanceSq(this.centerPos) <= getRadiusSq();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        centerPos = new BlockPos(compound.getInteger("centerX"), compound.getInteger("centerY"), compound.getInteger("centerZ"));
        radius = compound.getInteger("radius");
        timeRemaining = compound.getInteger("timeLeft");
        primaryCore = compound.getBoolean("isPrimary");
        owner = getWorld().getPlayerEntityByName(compound.getString("ownerName"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("centerX", centerPos != null ? centerPos.getX() : 0);
        compound.setInteger("centerY", centerPos != null ? centerPos.getY() : 0);
        compound.setInteger("centerZ", centerPos != null ? centerPos.getZ() : 0);
        compound.setInteger("radius", radius);
        compound.setInteger("timeLeft", timeRemaining);
        compound.setBoolean("isPrimary", primaryCore);
        compound.setString("ownerName", owner == null ? "null" : owner.getName());
        return compound;
    }
}
