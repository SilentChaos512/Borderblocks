package net.silentchaos512.borderblocks.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.event.ClientTicks;
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
        ClientTicks.INSTANCE.scheduleAction(() -> {
            EntityPlayer player = Borderblocks.proxy.getClientPlayer().world.getPlayerEntityByName(playerName);
            if (player != null) {
                PlayerData data = PlayerDataHandler.get(player);
                if (data != null)
                    data.readFromNBT(tags);
            }
        });
        return null;
    }
}
