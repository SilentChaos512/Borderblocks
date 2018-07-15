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

import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.item.ProgressionRelic;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum ProgressionTier implements IStringSerializable {
    WOOD(TextFormatting.GOLD),
    STONE(TextFormatting.DARK_GRAY),
    IRON(TextFormatting.WHITE),
    DIAMOND(TextFormatting.AQUA),
    ERIDIUM(TextFormatting.DARK_PURPLE);

    private final TextFormatting textColor;
    private ProgressionRelic relic;

    ProgressionTier(TextFormatting textColor) {
        this.textColor = textColor;
    }

    public String getFormattedName() {
        String key = "progressionTier." + name().toLowerCase();
        return textColor + Borderblocks.localization.getMiscText(key);
    }

    public static ProgressionTier byOrdinal(int i) {
        return values()[MathHelper.clamp(i, 0, values().length - 1)];
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Nonnull
    public ProgressionRelic getRelic() {
        if (relic == null) relic = new ProgressionRelic(this);
        return relic;
    }

    @Nonnull
    public ItemStack getRelicStack() {
        return new ItemStack(getRelic());
    }
}
