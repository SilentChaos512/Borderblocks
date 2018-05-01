package net.silentchaos512.borderblocks.lib.skill.action;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.block.PhaseBarrierBlock;
import net.silentchaos512.borderblocks.block.PhaseBarrierTileEntity;
import net.silentchaos512.borderblocks.init.ModBlocks;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.TimeHelper;

public class ActionSkillSiren extends ActionSkill {

  /** The chance (1/n) of a random barrier block being made into a core (redundancy!) */
  private static final int BARRIER_CORE_CHANCE = 40;
  private static final float DURATION_BASE = 10f;
  private static final int MAX_DISTANCE_BASE = 24;
  private static final double MAX_DISTANCE_PER_TIER = 4.8;
  private static final int RADIUS_BASE = 3;
  private static final float RADIUS_PER_TIER = (7f - RADIUS_BASE) / 4f;

  public ActionSkillSiren(String name) {

    super(name);
  }

  @Override
  public boolean activate(EntityPlayer player, ProgressionTier tier, BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown) {

    if ((hitPos == null || hitPos.equals(BlockPos.ORIGIN)) && !altKeyDown)
      return false;

    BlockPos center = null;

    if (altKeyDown) {
      // Alt key will center on player
      center = player.getPosition().up();
    } else {
      // Raytraced a block/entity
      center = hitPos.offset(hitSide);
    }

    if (center != null) {
      PlayerData data = PlayerDataHandler.get(player);
      float duration = getSkillDuration(data);
      return placeBarrier(data, player.world, center, getBarrierRadius(data), duration);
    }
    return false;
  }

  protected boolean placeBarrier(PlayerData data, World world, BlockPos center, int radius, float duration) {

    boolean corePlaced = false;
    for (int y = center.getY() + radius; y >= center.getY() - radius; --y)
      for (int x = center.getX() + radius; x >= center.getX() - radius; --x)
        for (int z = center.getZ() + radius; z >= center.getZ() - radius; --z)
          corePlaced |= tryPlaceBlock(data, world, new BlockPos(x, y, z), center, radius, duration, corePlaced);
    return corePlaced;
  }

  public void removeBarrier(World world, BlockPos center, int radius) {

    if (center == null)
      return;

    for (int y = center.getY() + radius; y >= center.getY() - radius; --y) {
      for (int x = center.getX() + radius; x >= center.getX() - radius; --x) {
        for (int z = center.getZ() + radius; z >= center.getZ() - radius; --z) {
          BlockPos pos = new BlockPos(x, y, z);
          if (world.getBlockState(pos).getBlock() instanceof PhaseBarrierBlock && isPointInSphere(pos, center, radius))
            world.setBlockToAir(pos);
        }
      }
    }
  }

  protected boolean tryPlaceBlock(PlayerData data, World world, BlockPos pos, BlockPos center, int radius, float duration, boolean corePlaced) {

    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();
    boolean blockReplaceable = world.isAirBlock(pos) || block instanceof PhaseBarrierBlock || block instanceof BlockTallGrass || block instanceof BlockDoublePlant;
    if (blockReplaceable && isPointInSphere(pos, center, radius)) {
      if (!corePlaced || Borderblocks.random.nextInt(BARRIER_CORE_CHANCE) == 0) {
        world.setBlockState(pos, ModBlocks.phaseBarrierCore.getDefaultState());
        PhaseBarrierTileEntity tile = (PhaseBarrierTileEntity) world.getTileEntity(pos);
        if (tile != null) {
          tile.setCenterPos(center);
          tile.setPrimaryCore(!corePlaced);
          tile.setRadius(radius);
          tile.setTimeRemaining(TimeHelper.ticksFromSeconds(duration));
          tile.setSkillProperties(data);
        }
      } else
        world.setBlockState(pos, ModBlocks.phaseBarrier.getDefaultState(), 2);
      return true;
    }
    return false;
  }

  protected boolean isPointInSphere(BlockPos pos, BlockPos center, int radius) {

    int x = pos.getX() - center.getX();
    int y = pos.getY() - center.getY();
    int z = pos.getZ() - center.getZ();
    int k = (x * x) + (y * y) + (z * z);
    int r2 = radius * radius;
    return k >= r2 - radius && k <= r2 + radius;
  }

//  protected @Nullable RayTraceResult rayTrace(EntityPlayer player) {
//
//    PlayerData data = PlayerDataHandler.get(player);
//    double distance = getSkillReach(data);
//    Vec3d vec = player.getPositionEyes(0);
//    Vec3d vec1 = player.getLook(0);
//    Vec3d vec2 = vec.addVector(vec1.x * distance, vec1.y * distance, vec1.z * distance);
//    return player.world.rayTraceBlocks(vec, vec2, false, false, true);
//  }

  @Override
  public float getCooldownTime() {

    return 30f;
  }

  public float getSkillDuration(PlayerData playerData) {

    int points = playerData.getPointsInSkill(SkillList.DURATION_BOOST_SIREN);
    return DURATION_BASE + points * SkillList.DURATION_BOOST_SIREN.getIncreasePerPoint();
  }

  public int getBarrierRadius(PlayerData playerData) {

    return Math.round(RADIUS_BASE + RADIUS_PER_TIER * playerData.getProgressionTier().ordinal());
  }

  @Override
  public double getSkillReach(PlayerData playerData) {

    return MAX_DISTANCE_BASE + MAX_DISTANCE_PER_TIER * playerData.getProgressionTier().ordinal();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<String> getTooltip(int investedPoints, PlayerData playerData) {

    ProgressionTier tier = playerData.getProgressionTier();
    List<String> list = new ArrayList<>();
    LocalizationHelper loc = Borderblocks.localization;
    list.add(TextFormatting.GOLD + loc.getLocalizedString("skill." + name + ".name"));
    list.add("");
    list.add(loc.getLocalizedString("skill." + name + ".desc1"));
    list.add("");
    list.add(loc.getLocalizedString("skill." + name + ".desc2",
        getBarrierRadius(playerData), (int) getSkillReach(playerData), (int) getSkillDuration(playerData), (int) getCooldownTime()));
    list.add("");
    list.add(loc.getLocalizedString("skill", "action.progressionTier", tier.getFormattedName()));
    list.add(String.format("Points: %d / %d", investedPoints, maxPoints));
    return list;
  }
}
