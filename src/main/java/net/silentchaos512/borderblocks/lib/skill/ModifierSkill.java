package net.silentchaos512.borderblocks.lib.skill;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.LocalizationHelper;

public class ModifierSkill extends Skill {

  static final double ARMOR_BOOST_SPEED_DECREASE = 0.02;

  protected IAttribute affectedAttribute;
  protected double amountPerPoint;
  protected int operation;
  protected UUID uuid;
  protected boolean alwaysApply = true;

  public ModifierSkill(String name, int maxPoints, IAttribute attribute, double amountPerPoint, int operation) {

    this(name, maxPoints, attribute, amountPerPoint, operation, true);
  }

  public ModifierSkill(String name, int maxPoints, IAttribute attribute, double amountPerPoint, int operation, boolean alwaysApply) {

    super(name, maxPoints);
    this.affectedAttribute = attribute;
    this.amountPerPoint = amountPerPoint;
    this.operation = operation;
    this.uuid = UUID.nameUUIDFromBytes(name.getBytes());
    this.alwaysApply = alwaysApply;
  }

  @Override
  public void applyTo(EntityPlayer player, int pointsInSkill) {

    IAttributeInstance attr = player.getEntityAttribute(affectedAttribute);
    applyModifier(attr, pointsInSkill, amountPerPoint, operation);

    if (this == SkillList.ARMOR_BOOST) {
      attr = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      applyModifier(attr, pointsInSkill, -ARMOR_BOOST_SPEED_DECREASE, 1);
    }
  }

  @Override
  public void removeFrom(EntityPlayer player) {

    IAttributeInstance attr = player.getEntityAttribute(affectedAttribute);
    removeModifier(attr);

    if (this == SkillList.ARMOR_BOOST) {
      attr = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      removeModifier(attr);
    }
  }

  private void applyModifier(IAttributeInstance attr, int pointsInSkill, double perPoint, int op) {

    if (attr != null) {
      // Get current and new modifier.
      AttributeModifier mod = attr.getModifier(uuid);
      AttributeModifier newMod = new AttributeModifier(uuid, name, perPoint * pointsInSkill, op);

      // Remove the old, apply the new.
      if (mod != null)
        attr.removeModifier(mod);
      attr.applyModifier(newMod);
    }
  }

  private void removeModifier(IAttributeInstance attr) {

    if (attr != null) {
      AttributeModifier mod = attr.getModifier(uuid);
      if (mod != null)
        attr.removeModifier(mod);
    }
  }

  @Override
  protected Object[] getDesc2Params(int investedPoints) {

    Object[] ret = new Object[isKillSkill ? 2 : 1];

    if (this == SkillList.ARMOR_BOOST) {
      float value1 = (float) (amountPerPoint * investedPoints);
      int value2 = (int) (100 * ARMOR_BOOST_SPEED_DECREASE * investedPoints);
      return new Object[] { value1, value2 };
    } else if (operation == 0) {
      ret[0] = (float) (amountPerPoint * investedPoints);
    } else {
      ret[0] = (int) (100 * amountPerPoint * investedPoints);
    }

    if (isKillSkill)
      ret[1] = (int) killSkillDuration;
    return ret;
  }
}
