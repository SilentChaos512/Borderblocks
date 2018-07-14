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

import net.minecraft.item.Item;
import net.silentchaos512.borderblocks.item.CraftingItem;
import net.silentchaos512.borderblocks.item.ProgressionRelic;
import net.silentchaos512.borderblocks.item.ScavMultiTool;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

public class ModItems implements IRegistrationHandler<Item> {

    public static ProgressionRelic progressionRelic = new ProgressionRelic();
    public static CraftingItem craftingItem = new CraftingItem();
    public static ScavMultiTool scavMultiTool = new ScavMultiTool();

    @Override
    public void registerAll(SRegistry reg) {
        reg.registerItem(progressionRelic);
        reg.registerItem(craftingItem);
        reg.registerItem(scavMultiTool);
    }
}
