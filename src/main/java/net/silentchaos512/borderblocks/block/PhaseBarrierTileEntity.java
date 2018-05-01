package net.silentchaos512.borderblocks.block;

import lombok.AccessLevel;
import lombok.Setter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.XPManager;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public class PhaseBarrierTileEntity extends TileEntity implements ITickable {

  @Setter(value = AccessLevel.PUBLIC)
  BlockPos centerPos;
  @Setter(value = AccessLevel.PUBLIC)
  int radius;
  @Setter(value = AccessLevel.PUBLIC)
  int timeRemaining = 100;
  @Setter(value = AccessLevel.PUBLIC)
  boolean primaryCore = false;

  EntityPlayer owner = null;
  int suffocateLevel = 0;
  int ignitionLevel = 0;

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
      for (EntityLivingBase mob : world.getEntities(EntityLivingBase.class, e -> e instanceof IMob && e.getDistanceSq(centerPos) < radius * radius)) {
        // Suffocate
        if (suffocateLevel > 0) {
          mob.attackEntityFrom(DamageSource.DROWN, 1.2f * suffocateLevel);
          if (mob.getHealth() <= 0f)
            XPManager.INSTANCE.awardXpForKill(mob, owner, true);
        }

        // Ignition
        if (ignitionLevel > 0 && mob instanceof EntityCreeper) {
          ((EntityCreeper) mob).ignite();
          // SkillEvents#mobGriefing will prevent any mob with this tag from damaging terrain.
          mob.getEntityData().setBoolean(SkillList.IGNITION.getName(), true);
        }

      }

      if (owner != null) {
        PlayerData data = PlayerDataHandler.get(owner);
        // Regen
        int regenLevel = data.getPointsInSkill(SkillList.REGEN_SIREN);
        if (regenLevel > 0) {
          SkillList.REGEN_SIREN.trigger(owner, regenLevel);
        }
      }
    }
  }

  public void setSkillProperties(PlayerData data) {

    owner = data.playerWR.get();
    suffocateLevel = data.getPointsInSkill(SkillList.BARRIER_SUFFOCATE);
    ignitionLevel = data.getPointsInSkill(SkillList.IGNITION);
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
