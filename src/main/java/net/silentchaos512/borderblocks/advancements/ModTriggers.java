package net.silentchaos512.borderblocks.advancements;

import net.silentchaos512.lib.registry.SRegistry;

public class ModTriggers {

  public static ClassChosenTrigger CLASS_CHOSEN;
  public static SkillPointAddedTrigger SKILL_POINT_ADDED;
  public static UseActionSkillTrigger USE_ACTION_SKILL;
  public static UseItemTrigger USE_ITEM;

  public static void init(SRegistry registry) {

    CLASS_CHOSEN = (ClassChosenTrigger) registry.registerAdvancementTrigger(new ClassChosenTrigger());
    SKILL_POINT_ADDED = (SkillPointAddedTrigger) registry.registerAdvancementTrigger(new SkillPointAddedTrigger());
    USE_ACTION_SKILL = (UseActionSkillTrigger) registry.registerAdvancementTrigger(new UseActionSkillTrigger());
    USE_ITEM = (UseItemTrigger) registry.registerAdvancementTrigger(new UseItemTrigger());
  }
}
