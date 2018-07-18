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

package net.silentchaos512.borderblocks.lib.skill;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BlockDropsSkill extends Skill {

    private final Map<IBlockState, ItemStack> bonusDrops = new HashMap<>();
    private final boolean matchBlockOnly;
    private final float dropChance;

    public BlockDropsSkill(String name, int maxPoints, boolean matchBlockOnly, float dropChance) throws IllegalArgumentException {
        super(name, maxPoints);
        this.matchBlockOnly = matchBlockOnly;
        this.dropChance = dropChance;
    }

    public boolean processDrops(HarvestDropsEvent event, int investedPoints, PlayerData data) {
        EntityPlayer player = event.getHarvester();
        IBlockState state = event.getState();
        Block block = state.getBlock();
        float chance = (float) (dropChance + 0.01 * player.getEntityAttribute(SharedMonsterAttributes.LUCK).getAttributeValue());
        boolean ret = false;

        for (Entry<IBlockState, ItemStack> entry : bonusDrops.entrySet()) {
            if (matches(state, entry.getKey()) && Borderblocks.random.nextFloat() < chance) {
                event.getDrops().add(entry.getValue().copy());
                ret = true;
            }
        }

        return ret;
    }

    private boolean matches(IBlockState state1, IBlockState state2) {
        if (matchBlockOnly)
            return state1.getBlock() == state2.getBlock();
        else
            return state1 == state2;
    }

    public void initDrops() {
        if (this == SkillList.FORAGER) {
            bonusDrops.put(Blocks.LEAVES.getDefaultState(), new ItemStack(Items.STICK));
            bonusDrops.put(Blocks.LEAVES2.getDefaultState(), new ItemStack(Items.STICK));
            bonusDrops.put(Blocks.DIRT.getDefaultState(), new ItemStack(Items.POTATO));
            bonusDrops.put(Blocks.GRASS.getDefaultState(), new ItemStack(Items.POTATO));
            bonusDrops.put(Blocks.STONE.getDefaultState(), new ItemStack(Items.FLINT));
        }
    }
}
