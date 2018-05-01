package net.silentchaos512.borderblocks.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.lib.block.BlockSL;

public class PhaseBarrierBlock extends BlockSL {

  public static final String NAME_BLOCK = "phase_barrier";
  public static final String NAME_CORE = "phase_barrier_core";

  public PhaseBarrierBlock(String name) {

    super(1, Borderblocks.MOD_ID, name, Material.BARRIER);
    setBlockUnbreakable();
    setResistance(6000001.0F);
  }

  @Override
  public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {

    if (entityIn instanceof EntityPlayer || (entityIn instanceof IEntityOwnable && ((IEntityOwnable) entityIn).getOwner() != null)
        || (entityIn instanceof EntityHorse && ((EntityHorse) entityIn).isTame()))
      return;
    super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {

    if (rand.nextInt(2 * (1 + 2 * Borderblocks.proxy.getParticleSettings())) == 0) {
      final float meanSpeed = 0.0125f;
      final double posX = pos.getX() + 0.5 + rand.nextGaussian() / 5;
      final double posY = pos.getY() - 0.5 + rand.nextGaussian() / 5;
      final double posZ = pos.getZ() + 0.5 + rand.nextGaussian() / 5;
      final double motionX = rand.nextGaussian() * meanSpeed;
      final double motionY = rand.nextGaussian() * meanSpeed;
      final double motionZ = rand.nextGaussian() * meanSpeed;
      worldIn.spawnParticle(EnumParticleTypes.PORTAL, posX, posY, posZ, motionX, motionY, motionZ);
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public BlockRenderLayer getBlockLayer() {

    return BlockRenderLayer.TRANSLUCENT;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {

    return false;
  }

  @Override
  public boolean isFullCube(IBlockState state) {

    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess,
      BlockPos pos, EnumFacing side) {

    IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
    Block block = iblockstate.getBlock();

    if (this == block && blockState != iblockstate)
      return true;

    return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
  }
}
