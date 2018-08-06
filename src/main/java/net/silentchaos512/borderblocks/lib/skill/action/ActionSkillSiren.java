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

package net.silentchaos512.borderblocks.lib.skill.action;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
import net.silentchaos512.lib.util.TimeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActionSkillSiren extends ActionSkill {
    /**
     * The chance (1/n) of a random barrier block being made into a core (redundancy!)
     */
    private static final int BARRIER_CORE_CHANCE = 40;
    private static final float DURATION_BASE = 10f;
    private static final int MAX_DISTANCE_BASE = 24;
    private static final double MAX_DISTANCE_PER_TIER = 4.8;
    private static final int RADIUS_BASE = 3;
    private static final float RADIUS_PER_TIER = (7f - RADIUS_BASE) / 4f;

    public ActionSkillSiren(ResourceLocation name) {
        super(name);
    }

    @Override
    public boolean activate(EntityPlayer player, ProgressionTier tier, BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown) {
        if ((hitPos == null || hitPos.equals(BlockPos.ORIGIN)) && !altKeyDown)
            return false;

        BlockPos center;

        if (altKeyDown) {
            // Alt key will center on player
            center = player.getPosition().up();
        } else {
            // Raytraced a block/entity
            center = hitPos.offset(hitSide);
        }

        PlayerData data = PlayerDataHandler.get(player);
        float duration = getSkillDuration(data);
        int radius = getBarrierRadius(data);
        boolean success = placeBarrier(data, player.world, center, radius, duration);
        if (success && data != null && data.getPointsInSkill(SkillList.BARRIER_TELEPORT) > 0) {
            if (!altKeyDown)
                tryTeleportIntoBarrier(player, center, radius);
            // Brief damage resistance
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 2, true, false));
        }
        return success;
    }

    private boolean placeBarrier(PlayerData data, World world, BlockPos center, int radius, float duration) {
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

    private boolean tryPlaceBlock(PlayerData data, World world, BlockPos pos, BlockPos center, int radius, float duration, boolean corePlaced) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (blockReplaceable(world, pos, block) && isPointInSphere(pos, center, radius)) {
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

    private boolean blockReplaceable(World world, BlockPos pos, Block block) {
        return world.isAirBlock(pos) || block instanceof PhaseBarrierBlock || block instanceof BlockTallGrass
                || block instanceof BlockDoublePlant || block == Blocks.WATER || block == Blocks.FLOWING_WATER;
    }

    private boolean tryTeleportIntoBarrier(EntityPlayer player, BlockPos center, int radius) {
        Random rand = Borderblocks.random;
        int r = radius - 2;
        double x = center.getX();
        double y = center.getY() + (radius - 1);
        double z = center.getZ();

        for (int i = 0; i < 50; ++i)
            if (tryTeleportPlayerTo(player, center, radius, x + rand.nextInt(2 * r) - r, y, z + rand.nextInt(2 * r) - r))
                return true;
        return false;
    }

    private boolean tryTeleportPlayerTo(EntityPlayer player, BlockPos center, int radius, double x, double y, double z) {
        double d0 = player.posX;
        double d1 = player.posY;
        double d2 = player.posZ;
        player.posX = x;
        player.posY = y;
        player.posZ = z;
        boolean flag = false;
        BlockPos blockpos = new BlockPos(player);
        World world = player.world;
        Random random = player.getRNG();

        if (world.isBlockLoaded(blockpos)) {
            boolean flag1 = false;

            while (!flag1 && blockpos.getY() > Math.max(0, center.getY() - (radius - 1))) {
                BlockPos blockpos1 = blockpos.down();
                IBlockState iblockstate = world.getBlockState(blockpos1);

                if (iblockstate.getMaterial().blocksMovement()) {
                    flag1 = true;
                } else {
                    --player.posY;
                    blockpos = blockpos1;
                }
            }

            BlockPos target = player.getPosition().down();
            Borderblocks.log.debug(flag1, world.getBlockState(target), world.isBlockFullCube(target));
            if (flag1 && world.isBlockFullCube(target) && !(world.getBlockState(target).getBlock() instanceof PhaseBarrierBlock)) {
                player.setPositionAndUpdate(player.posX + 0.5, player.posY, player.posZ + 0.5);

                if (world.getCollisionBoxes(player, player.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(player.getEntityBoundingBox())) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            player.setPositionAndUpdate(d0, d1, d2);
            return false;
        } else {
            for (int j = 0; j < 128; ++j) {
                double d6 = (double) j / 127.0D;
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d3 = d0 + (player.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double) player.width * 2.0D;
                double d4 = d1 + (player.posY - d1) * d6 + random.nextDouble() * (double) player.height;
                double d5 = d2 + (player.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double) player.width * 2.0D;
                world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double) f, (double) f1, (double) f2);
            }

            return true;
        }
    }

    private boolean isPointInSphere(BlockPos pos, BlockPos center, int radius) {
        int x = pos.getX() - center.getX();
        int y = pos.getY() - center.getY();
        int z = pos.getZ() - center.getZ();
        int k = (x * x) + (y * y) + (z * z);
        int r2 = radius * radius;
        return k >= r2 - radius && k <= r2 + radius;
    }

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
        list.add(TextFormatting.GOLD + Borderblocks.i18n.translate("skill." + name + ".name"));
        list.add("");
        list.add(Borderblocks.i18n.translate("skill." + name + ".desc1"));
        list.add("");
        list.add(Borderblocks.i18n.translate("skill." + name + ".desc2",
                getBarrierRadius(playerData), (int) getSkillReach(playerData), (int) getSkillDuration(playerData), (int) getCooldownTime()));
        list.add("");
        list.add(Borderblocks.i18n.translate("skill", "action.progressionTier", tier.getFormattedName()));
        list.add(String.format("Points: %d / %d", investedPoints, maxPoints));
        return list;
    }
}
