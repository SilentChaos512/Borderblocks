package net.silentchaos512.borderblocks.lib.skill;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public class RegenSkill extends Skill {

  @Getter(value = AccessLevel.PUBLIC)
  float regenRate;

  public RegenSkill(String name, int maxPoints, float regenRate) throws IllegalArgumentException {

    super(name, maxPoints);
    this.regenRate = regenRate;
  }
  
  public void trigger(EntityPlayer player, int investedPoints) {

    player.heal(regenRate * investedPoints * player.getMaxHealth());
  }

  @Override
  protected Object[] getDesc2Params(int investedPoints) {

    return new Object[] { 100f * regenRate * investedPoints };
  }
}
