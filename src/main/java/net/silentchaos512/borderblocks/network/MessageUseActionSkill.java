package net.silentchaos512.borderblocks.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.network.MessageSL;

public class MessageUseActionSkill extends MessageSL {

  public String playerName;
  public boolean altKeyDown;

  // Raytrace
  public BlockPos hitPos;
  public int hitSide;

  public MessageUseActionSkill() {

  }

  public MessageUseActionSkill(EntityPlayer player, BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown) {

    this.playerName = player.getName();
    this.altKeyDown = altKeyDown;

    this.hitPos = hitPos == null ? BlockPos.ORIGIN : hitPos;
    this.hitSide = hitSide.ordinal();
  }

  @Override
  public IMessage handleMessage(MessageContext context) {

    EntityPlayer player = context.getServerHandler().player.world.getPlayerEntityByName(playerName);
    PlayerData data = PlayerDataHandler.get(player);

    BlockPos pos = hitPos == BlockPos.ORIGIN ? null : hitPos;
    EnumFacing side = EnumFacing.values()[MathHelper.clamp(hitSide, 0, EnumFacing.values().length)];

    data.activateActionSkill(pos, side, this.altKeyDown);

    return null;
  }

}
