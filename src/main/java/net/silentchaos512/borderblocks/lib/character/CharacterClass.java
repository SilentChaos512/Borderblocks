package net.silentchaos512.borderblocks.lib.character;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.lib.skill.action.ActionSkill;

public class CharacterClass {

  private static final String NAME_UNDEFINED = Borderblocks.RESOURCE_PREFIX + "_undefined_";
  private static final String NAME_SCAVENGER = Borderblocks.RESOURCE_PREFIX + "scavenger";
  private static final String NAME_SIREN = Borderblocks.RESOURCE_PREFIX + "siren";

  public static final List<CharacterClass> ALL_CLASSES = new ArrayList<>();

  public static final CharacterClass CLASS_UNDEFINED = new CharacterClass(NAME_UNDEFINED);
  public static final CharacterClass CLASS_SCAVENGER = new CharacterClass(NAME_SCAVENGER);
  public static final CharacterClass CLASS_SIREN = new CharacterClass(NAME_SIREN);

  @Getter(value = AccessLevel.PUBLIC)
  String className;

  @Getter(value = AccessLevel.PUBLIC)
  SkillTreeBranch branchLeft = new SkillTreeBranch();
  @Getter(value = AccessLevel.PUBLIC)
  SkillTreeBranch branchMiddle = new SkillTreeBranch();
  @Getter(value = AccessLevel.PUBLIC)
  SkillTreeBranch branchRight = new SkillTreeBranch();

  @Getter(value = AccessLevel.PUBLIC)
  ActionSkill actionSkill = null;

  public CharacterClass(String className) {

    this.className = className;

    if (className.equals(NAME_UNDEFINED)) {
      actionSkill = SkillList.ACTION_UNDEFINED;
      return;
    }

    if (className.equals(NAME_SCAVENGER)) {
      branchLeft.addSkill(SkillList.HEALTH_BOOST_SCAV, 0);
      branchLeft.addSkill(SkillList.ARMOR_BOOST, 0);
      branchLeft.addSkill(SkillList.SPEED_BOOST, 1);
      branchLeft.addSkill(SkillList.MULTI_TOOL_REPAIR, 1);
      branchLeft.addSkill(SkillList.COOLDOWN_SCAV, 1);
      branchLeft.addSkill(SkillList.PLACEHOLDER, 2);
      branchLeft.addSkill(SkillList.PLACEHOLDER, 2);
      branchLeft.addSkill(SkillList.MULTI_TOOL_SILKTOUCH, 3);

      branchMiddle.addSkill(SkillList.BREAK_SPEED_ORES, 0);
      branchMiddle.addSkill(SkillList.BREAK_SPEED_LOGS, 0);
      branchMiddle.addSkill(SkillList.LUCK_BOOST, 1);
      branchMiddle.addSkill(SkillList.FORAGER, 1);
      branchMiddle.addSkill(SkillList.FALL_DAMAGE_DOWN_SCAV, 1);
      branchMiddle.addSkill(SkillList.BREAK_SPEED_GENERIC, 2);
      branchMiddle.addSkill(SkillList.DURATION_BOOST_SCAV, 2);
      branchMiddle.addSkill(SkillList.MULTI_TOOL_FORTUNE, 3);

      actionSkill = SkillList.ACTION_SCAVENGER;
    } else if (className.equals(NAME_SIREN)) {
      branchLeft.addSkill(SkillList.DURATION_BOOST_SIREN, 0);
      branchLeft.addSkill(SkillList.HEALTH_BOOST_SIREN, 0);
      branchLeft.addSkill(SkillList.REGEN_SIREN, 1);
      branchLeft.addSkill(SkillList.ARROW_DEFLECTION, 1);
      branchLeft.addSkill(SkillList.PLACEHOLDER, 2);
      branchLeft.addSkill(SkillList.IGNITION, 2);
      branchLeft.addSkill(SkillList.PLACEHOLDER, 2);

      branchMiddle.addSkill(SkillList.COOLDOWN_SIREN, 0);
      branchMiddle.addSkill(SkillList.BARRIER_SUFFOCATE, 0);
      branchMiddle.addSkill(SkillList.ATTACK_SPEED_BOOST_K, 1);
      branchMiddle.addSkill(SkillList.FIRE_DAMAGE_DOWN, 1);
      branchMiddle.addSkill(SkillList.PLACEHOLDER, 2);
      branchMiddle.addSkill(SkillList.PLACEHOLDER, 2);
      branchMiddle.addSkill(SkillList.PLACEHOLDER, 2);

      actionSkill = SkillList.ACTION_SIREN;
    }

    ALL_CLASSES.add(this);
  }

  public @Nullable SkillTreeBranch getBranchOfSkill(Skill skill) {

    if (branchLeft.containsSkill(skill))
      return branchLeft;
    if (branchMiddle.containsSkill(skill))
      return branchMiddle;
    if (branchRight.containsSkill(skill))
      return branchRight;
    return null;
  }

  public String getLocalizedName() {

    return Borderblocks.localization.getLocalizedString("class." + className + ".name");
  }

  public static CharacterClass getByName(String className) {

    for (CharacterClass charClass : ALL_CLASSES)
      if (charClass.className.equals(className))
        return charClass;
    return CLASS_UNDEFINED;
  }

  public static void init() {

    // NO-OP
  }
}
