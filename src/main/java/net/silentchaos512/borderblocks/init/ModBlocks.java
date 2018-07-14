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

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.silentchaos512.borderblocks.block.EridiumBlock;
import net.silentchaos512.borderblocks.block.EridiumOre;
import net.silentchaos512.borderblocks.block.PhaseBarrierBlock;
import net.silentchaos512.borderblocks.block.PhaseBarrierCore;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

public class ModBlocks implements IRegistrationHandler<Block> {

    public static EridiumOre eridiumOre = new EridiumOre();
    public static EridiumBlock eridiumBlock = new EridiumBlock();
    public static PhaseBarrierBlock phaseBarrier = new PhaseBarrierBlock();
    public static PhaseBarrierCore phaseBarrierCore = new PhaseBarrierCore();

    @Override
    public void registerAll(SRegistry reg) {
        reg.registerBlock(eridiumOre, "eridium_ore", new ItemBlock(eridiumOre));
        reg.registerBlock(eridiumBlock, "eridium_block", new ItemBlock(eridiumBlock));
        reg.registerBlock(phaseBarrier, "phase_barrier", new ItemBlock(phaseBarrier)).setCreativeTab(null);
        reg.registerBlock(phaseBarrierCore, "phase_barrier_core", new ItemBlock(phaseBarrierCore)).setCreativeTab(null);
    }
}
