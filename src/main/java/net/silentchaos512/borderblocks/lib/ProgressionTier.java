package net.silentchaos512.borderblocks.lib;

import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.borderblocks.Borderblocks;

public enum ProgressionTier {

  WOOD(TextFormatting.GOLD),
  STONE(TextFormatting.DARK_GRAY),
  IRON(TextFormatting.WHITE),
  DIAMOND(TextFormatting.AQUA),
  ERIDIUM(TextFormatting.DARK_PURPLE);

  final TextFormatting textColor;

  private ProgressionTier(TextFormatting textColor) {

    this.textColor = textColor;
  }

  public static ProgressionTier byOrdinal(int value) {

    if (value < 0 || value >= values().length)
      return WOOD;
    return values()[value];
  }

  public String getFormattedName() {

    String key = "progressionTier." + name().toLowerCase();
    return textColor + Borderblocks.localization.getMiscText(key);
  }
}
