package net.silentchaos512.borderblocks.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.client.TextEffectDisplayHandler;
import net.silentchaos512.borderblocks.init.ModSounds;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.lib.network.MessageSL;

public class MessageXpGain extends MessageSL {
    public int amount;
    public boolean levelUpped;

    public MessageXpGain() {
    }

    public MessageXpGain(int amount, boolean levelUpped) {
        this.amount = amount;
        this.levelUpped = levelUpped;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage handleMessage(MessageContext ctx) {
        // This message exists mostly for TextEffects.
        ClientTicks.INSTANCE.scheduleAction(() -> {
            EntityPlayer player = Borderblocks.proxy.getClientPlayer();

            // TextEffects
            TextEffectDisplayHandler.INSTANCE.addXpEffect(amount);
            if (levelUpped) {
                TextEffectDisplayHandler.INSTANCE.addLevelUpEffect();
                player.playSound(ModSounds.LEVEL_UP, 0.7f, 1.0f);
            }

            // Also sync XP/level. Guess that's better than sending an entire data sync packet.
            PlayerData data = PlayerDataHandler.get(player);
            if (data != null)
                data.addXp(amount);
        });
        return null;
    }

}
