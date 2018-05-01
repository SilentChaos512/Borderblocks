package net.silentchaos512.borderblocks.world;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.init.ModBlocks;
import net.silentchaos512.lib.config.ConfigOptionOreGen;
import net.silentchaos512.lib.world.WorldGeneratorSL;

public class WorldGeneratorBB extends WorldGeneratorSL {

  static final Predicate PREDICATE_STONE = BlockMatcher.forBlock(Blocks.STONE);

  public WorldGeneratorBB() {

    super(false, Borderblocks.MOD_ID, Borderblocks.BUILD_NUM);
  }

  @Override
  protected void generateSurface(World world, Random random, int posX, int posZ) {

    generateOres(world, random, posX, posZ, Config.ERIDIUM_ORE_GEN, PREDICATE_STONE);
  }

  protected void generateOres(World world, Random random, int posX, int posZ,
      ConfigOptionOreGen config, Predicate predicate) {

    final int dimension = world.provider.getDimension();

    if (config.isEnabled() && config.canSpawnInDimension(dimension)) {
      int veinCount = config.getVeinCount(random);
      int veinSize = config.veinSize;

      for (int i = 0; i < veinCount; ++i) {
        BlockPos pos = config.getRandomPos(random, posX, posZ);
        IBlockState state = getState(config);
        new WorldGenEridium(state, veinSize, predicate).generate(world, random, pos);
      }
    }
  }

  protected IBlockState getState(ConfigOptionOreGen config) {

    if (config == Config.ERIDIUM_ORE_GEN)
      return ModBlocks.eridiumOre.getDefaultState();
    else
      Borderblocks.log.severe("WorldGeneratorBB - Unknown ore config: " + config.name);
    return null;
  }
}
