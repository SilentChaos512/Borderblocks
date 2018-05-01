package net.silentchaos512.borderblocks.lib.skill.action;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public abstract class ActionSkill extends Skill {

  public ActionSkill(String name) {

    super(name, 1);
  }

  public abstract boolean activate(EntityPlayer player, ProgressionTier tier, @Nullable BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown);

  public abstract float getCooldownTime();

  public abstract float getSkillDuration(PlayerData data);

  public double getSkillReach(PlayerData data) {

    return 16.0;
  }
}
