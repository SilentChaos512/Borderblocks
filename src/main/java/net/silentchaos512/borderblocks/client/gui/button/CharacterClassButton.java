package net.silentchaos512.borderblocks.client.gui.button;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.gui.GuiButton;
import net.silentchaos512.borderblocks.lib.character.CharacterClass;

public class CharacterClassButton extends GuiButton {

  @Getter(value = AccessLevel.PUBLIC)
  CharacterClass characterClass;

  public CharacterClassButton(int buttonId, int x, int y, int widthIn, int heightIn, CharacterClass characterClass) {

    super(buttonId, x, y, widthIn, heightIn, characterClass.getLocalizedName());
    this.characterClass = characterClass;
  }
}
