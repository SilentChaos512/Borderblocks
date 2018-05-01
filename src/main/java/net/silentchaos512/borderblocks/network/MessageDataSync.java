package net.silentchaos512.borderblocks.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.client.ClientTickHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.network.MessageSL;

public class MessageDataSync extends MessageSL {

  public NBTTagCompound tags;
  public String playerName;

  public MessageDataSync() {

  }

  public MessageDataSync(PlayerData data, EntityPlayer player) {

    tags = new NBTTagCompound();
    data.writeToNBT(tags);
    this.playerName = player.getName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IMessage handleMessage(MessageContext context) {

    ClientTickHandler.scheduledActions.add(() -> {
      EntityPlayer player = Borderblocks.proxy.getClientPlayer().world.getPlayerEntityByName(playerName);
      if (player != null) {
        PlayerData data = PlayerDataHandler.get(player);
        data.readFromNBT(tags);

        // Set players health and max health.
        // if (ConfigScalingHealth.ALLOW_PLAYER_MODIFIED_HEALTH)
        // ModifierHandler.setMaxHealth(player, data.getMaxHealth(), 0);
        // if (data.getHealth() > 0f)
        // player.setHealth(data.getHealth());
      }
    });

    return null;
  }
}
