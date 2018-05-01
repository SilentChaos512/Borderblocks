package net.silentchaos512.borderblocks.init;

import net.minecraft.block.Block;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.block.EridiumBlock;
import net.silentchaos512.borderblocks.block.EridiumOre;
import net.silentchaos512.borderblocks.block.PhaseBarrierBlock;
import net.silentchaos512.borderblocks.block.PhaseBarrierCore;
import net.silentchaos512.borderblocks.block.PhaseBarrierTileEntity;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

public class ModBlocks implements IRegistrationHandler<Block> {

  public static EridiumOre eridiumOre = new EridiumOre();
  public static EridiumBlock eridiumBlock = new EridiumBlock();
  public static PhaseBarrierBlock phaseBarrier = new PhaseBarrierBlock(PhaseBarrierBlock.NAME_BLOCK);
  public static PhaseBarrierCore phaseBarrierCore = new PhaseBarrierCore(PhaseBarrierBlock.NAME_CORE);

  @Override
  public void registerAll(SRegistry reg) {

    reg.registerBlock(eridiumOre).setCreativeTab(Borderblocks.CREATIVE_TAB);
    reg.registerBlock(eridiumBlock).setCreativeTab(Borderblocks.CREATIVE_TAB);
    reg.registerBlock(phaseBarrier).setCreativeTab(null);
    reg.registerBlock(phaseBarrierCore).setCreativeTab(null);

    reg.registerTileEntity(PhaseBarrierTileEntity.class, PhaseBarrierBlock.NAME_BLOCK);
  }
}
