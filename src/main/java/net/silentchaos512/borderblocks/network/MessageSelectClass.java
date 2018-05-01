package net.silentchaos512.borderblocks.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.silentchaos512.borderblocks.advancements.ModTriggers;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.character.CharacterClass;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.network.MessageSL;

public class MessageSelectClass extends MessageSL {

  public String playerName;
  public String className;

  public MessageSelectClass() {

  }

  public MessageSelectClass(EntityPlayer player, CharacterClass characterClass) {

    this.playerName = player.getName();
    this.className = characterClass.getClassName();
  }

  @Override
  public IMessage handleMessage(MessageContext context) {

    EntityPlayer player = context.getServerHandler().player.world.getPlayerEntityByName(playerName);
    PlayerData data = PlayerDataHandler.get(player);

    data.setCharacterClass(CharacterClass.getByName(className));
    data.setProgressionTier(ProgressionTier.WOOD);

    ModTriggers.CLASS_CHOSEN.trigger((EntityPlayerMP) player);

    return null;
  }
}
