package net.silentchaos512.borderblocks.lib.skill;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.LocalizationHelper;

public class Skill {

  public static final ResourceLocation TEXTURE_SKILLS = new ResourceLocation(Borderblocks.MOD_ID, "textures/gui/skills.png");

  @Getter(value = AccessLevel.PUBLIC)
  protected String name;
  @Getter(value = AccessLevel.PUBLIC)
  protected int maxPoints;

  @Getter(value = AccessLevel.PUBLIC)
  protected boolean isKillSkill = false;
  @Getter(value = AccessLevel.PUBLIC)
  protected float killSkillDuration = 0f;

  protected ResourceLocation texture;
  protected int textureIndex = 0;

  public Skill(String name, int maxPoints) throws IllegalArgumentException {

    this.name = name;
    this.maxPoints = maxPoints;

    if (maxPoints <= 0)
      throw new IllegalArgumentException("maxPoints must be greater than zero.");

    this.texture = TEXTURE_SKILLS;

    SkillList.ALL_SKILLS.add(this);
  }

  public void applyTo(EntityPlayer player, int pointsInSkill) {

  }

  public void removeFrom(EntityPlayer player) {

  }

  @SideOnly(Side.CLIENT)
  public List<String> getTooltip(int investedPoints, PlayerData playerData) {

    List<String> list = new ArrayList<>();
    LocalizationHelper loc = Borderblocks.localization;
    list.add(TextFormatting.GOLD + loc.getLocalizedString("skill." + name + ".name"));
    list.add("");
    list.add(loc.getLocalizedString("skill." + name + ".desc1"));
    list.add("");

    if (investedPoints > 0) {
    list.add(getDesc2(investedPoints));
    list.add("");
    }
    if (investedPoints < maxPoints) {
      list.add(loc.getMiscText("nextLevel"));
      list.add(getDesc2(investedPoints + 1));
      list.add("");
    }

    list.add(String.format("Points: %d / %d", investedPoints, maxPoints));
    return list;
  }

  public <T extends Skill> T setKillSkill(float duration) {

    this.isKillSkill = duration > 0f;
    this.killSkillDuration = duration;
    return (T) this;
  }

  @SideOnly(Side.CLIENT)
  public ResourceLocation getTexture() {

    return TEXTURE_SKILLS;
  }

  public <T extends Skill> T setTexture(ResourceLocation texture) {

    this.texture = texture;
    return (T) this;
  }

  @SideOnly(Side.CLIENT)
  public int getTextureIndex() {

    return textureIndex;
  }

  public <T extends Skill> T setTextureIndex(int index) {

    this.textureIndex = index;
    return (T) this;
  }

  protected String getDesc2(int investedPoints) {

    String key = "skill." + name + ".desc2";
    return TextFormatting.AQUA + Borderblocks.localization.getLocalizedString(key, getDesc2Params(investedPoints));
  }

  protected Object[] getDesc2Params(int investedPoints) {

    return new Object[] { investedPoints };
  }
}
