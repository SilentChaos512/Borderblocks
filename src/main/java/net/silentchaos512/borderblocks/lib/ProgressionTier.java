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

package net.silentchaos512.borderblocks.lib;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.borderblocks.Borderblocks;

import java.util.Locale;

public enum ProgressionTier implements IStringSerializable {
    WOOD(TextFormatting.GOLD),
    STONE(TextFormatting.DARK_GRAY),
    IRON(TextFormatting.WHITE),
    DIAMOND(TextFormatting.AQUA),
    ERIDIUM(TextFormatting.DARK_PURPLE);

    final TextFormatting textColor;

    ProgressionTier(TextFormatting textColor) {
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

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
