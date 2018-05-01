package net.silentchaos512.borderblocks.lib.skill;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.LocalizationHelper;

public class CooldownBonusSkill extends Skill {

  protected float amountPerPoint;

  public CooldownBonusSkill(String name, int maxPoints, float amountPerPoint) {

    super(name, maxPoints);
    this.amountPerPoint = amountPerPoint;
  }

  @Override
  public void applyTo(EntityPlayer player, int pointsInSkill) {

    PlayerData data = PlayerDataHandler.get(player);
    data.addCooldownBonus(amountPerPoint * pointsInSkill);
  }

  @Override
  protected Object[] getDesc2Params(int investedPoints) {

    return new Object[] { (int) (100 * amountPerPoint * investedPoints) };
  }
}
