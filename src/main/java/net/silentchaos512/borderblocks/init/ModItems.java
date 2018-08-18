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

package net.silentchaos512.borderblocks.init;

import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.borderblocks.item.CraftingItems;
import net.silentchaos512.borderblocks.item.ScavMultiTool;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.lib.item.IEnumItems;
import net.silentchaos512.lib.registry.SRegistry;

public class ModItems {
    public static void registerAll(SRegistry reg) {
        IEnumItems.RegistrationHelper enumItems = new IEnumItems.RegistrationHelper(reg);

        enumItems.registerItemsGenericEnum(ProgressionTier::getRelic, e -> "progression_relic_" + e.getName(), ProgressionTier.class);
        enumItems.registerItems(CraftingItems.values());
        enumItems.registerItemsGenericEnum(ScavMultiTool::new, e -> "scav_multi_tool_" + e.getName(), ProgressionTier.class);

        addOreDict();
    }

    private static void addOreDict() {
        OreDictionary.registerOre("ingotEridium", CraftingItems.ERIDIUM_INGOT.getStack());
    }
}
