package net.silentchaos512.borderblocks.lib.skill;

import lombok.AccessLevel;
import lombok.Getter;

public class DurationSkill extends Skill {

  @Getter(value = AccessLevel.PUBLIC)
  float increasePerPoint;

  public DurationSkill(String name, int maxPoints, float increasePerPoint) {

    super(name, maxPoints);
    this.increasePerPoint = increasePerPoint;
  }

  @Override
  protected Object[] getDesc2Params(int investedPoints) {

    return new Object[] { (int) (increasePerPoint * investedPoints) };
  }
}
