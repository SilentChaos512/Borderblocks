package net.silentchaos512.borderblocks.lib.character;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public class SkillTreeBranch {

  List<SkillTier> tiers = new ArrayList<>();

  public void addSkill(Skill skill, int tier) {

    // Grab the tier, creating any missing ones.
    SkillTier skillTier = getTier(tier);
    // We could check for duplicates here, probably better to do at a higher level.
    skillTier.skills.add(skill);
  }

  public boolean containsSkill(Skill skill) {

    for (SkillTier skillTier : tiers)
      for (Skill skillInBranch : skillTier.skills)
        if (skill.equals(skillInBranch))
          return true;
    return false;
  }

  public int getTierOfSkill(Skill skill) {

    for (SkillTier skillTier : tiers)
      for (Skill skillInBranch : skillTier.skills)
        if (skill.equals(skillInBranch))
          return tiers.indexOf(skillTier);
    return -1;
  }

  public void setPointsToClearTier(int tier, int pointsRequired) {

    getTier(tier).pointsToClear = pointsRequired;
  }

  public int getPointsInvested(EntityPlayer player) {

    int total = 0;
    PlayerData data = PlayerDataHandler.get(player);

    for (Entry<Skill, Integer> entry : data.getSkills().entrySet()) {
      Skill skill = entry.getKey();
      int level = entry.getValue();
      if (this.containsSkill(skill))
        total += level;
    }

    return total;
  }

  public int getHighestAccessibleTier(EntityPlayer player) {

    // Has player unlocked their action skill?
    PlayerData data = PlayerDataHandler.get(player);
    if (data.getSkills().isEmpty()) {
      // We'll assume that no skills means no action skill.
      return -1;
    }

    int points = getPointsInvested(player);
    int tier = 0;

    for (; tier < tiers.size(); ++tier) {
      points -= getTier(tier).pointsToClear;
      if (points < 0)
        return tier;
    }

    return tier;
  }

  public int getTierCount() {

    return tiers.size();
  }

  public SkillTier getTier(int tier) {

    // Tier already exists?
    if (tier < tiers.size())
      return tiers.get(tier);

    // Create the tier, and any below it.
    for (int i = tiers.size(); i <= tier; ++i) {
      tiers.add(new SkillTier());
    }
    return tiers.get(tier);
  }

  public static class SkillTier implements Iterable<Skill> {

    List<Skill> skills = new ArrayList<>();
    int pointsToClear = 5;

    public int getSkillCount() {

      return skills.size();
    }

    @Override
    public Iterator<Skill> iterator() {

      return skills.iterator();
    }
  }
}
