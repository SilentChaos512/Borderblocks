package net.silentchaos512.borderblocks.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.network.MessageSL;

public class MessageSkillRespec extends MessageSL {

  public String playerName;

  public MessageSkillRespec() {

  }

  public MessageSkillRespec(EntityPlayer player) {

    this.playerName = player.getName();
  }

  @Override
  public IMessage handleMessage(MessageContext context) {

    EntityPlayer player = context.getServerHandler().player.world.getPlayerEntityByName(playerName);
    PlayerData data = PlayerDataHandler.get(player);

    data.respecSkills();

    return null;
  }
}
