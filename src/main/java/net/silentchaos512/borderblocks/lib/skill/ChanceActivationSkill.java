package net.silentchaos512.borderblocks.lib.skill;

import lombok.AccessLevel;
import lombok.Getter;

public class ChanceActivationSkill extends Skill {

  @Getter(value = AccessLevel.PUBLIC)
  float chancePerPoint;

  public ChanceActivationSkill(String name, int maxPoints, float chancePerPoint) throws IllegalArgumentException {

    super(name, maxPoints);
    this.chancePerPoint = chancePerPoint;
  }

  protected Object[] getDesc2Params(int investedPoints) {

    return new Object[] { (int) (100 * chancePerPoint * investedPoints) };
  }
}
