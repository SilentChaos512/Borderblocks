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

package net.silentchaos512.borderblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.silentchaos512.borderblocks.init.ModItems;

import java.awt.*;

public class ColorHandlers {
    public static void init() {
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        // Progression Relics
        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 1) {
                float period = 40f;
                return Color.HSBtoRGB((ClientTickHandler.ticksInGame % period) / period, 0.6f, 1.0f);
            }
            return 0xFFFFFF;
        }, ModItems.progressionRelic);
    }
}
