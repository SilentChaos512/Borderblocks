/*
 * Borderblocks
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
