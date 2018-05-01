package net.silentchaos512.borderblocks.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PhaseBarrierCore extends PhaseBarrierBlock implements ITileEntityProvider {

  public PhaseBarrierCore(String name) {

    super(name);
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {

    return new PhaseBarrierTileEntity();
  }
}
