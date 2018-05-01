package net.silentchaos512.borderblocks.lib.skill;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import net.silentchaos512.lib.util.StackHelper;

public class BlockDropsSkill extends Skill {

  final Map<IBlockState, ItemStack> bonusDrops = new HashMap<>();
  final boolean matchBlockOnly;
  final float dropChance;

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
        event.getDrops().add(StackHelper.safeCopy(entry.getValue()));
        ret = true;
      }
    }

    return ret;
  }

  protected boolean matches(IBlockState state1, IBlockState state2) {

    if (matchBlockOnly)
      return state1.getBlock() == state2.getBlock();
    else
      return state1.equals(state2); // TODO: Is this right?
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
