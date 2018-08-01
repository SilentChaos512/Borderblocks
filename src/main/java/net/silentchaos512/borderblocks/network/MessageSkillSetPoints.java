package net.silentchaos512.borderblocks.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.silentchaos512.borderblocks.advancements.ModTriggers;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.network.MessageSL;

public class MessageSkillSetPoints extends MessageSL {

  public String playerName;
  public String skillName;
  public int amount;

  public MessageSkillSetPoints() {

  }

  public MessageSkillSetPoints(EntityPlayer player, Skill skill, int amount) {

    this.playerName = player.getName();
    this.skillName = skill.getName().toString();
    this.amount = amount;
  }

  @Override
  public IMessage handleMessage(MessageContext context) {

    EntityPlayer player = context.getServerHandler().player.world.getPlayerEntityByName(playerName);
    PlayerData data = PlayerDataHandler.get(player);
    Skill skill = SkillList.getByName(skillName);

    if (data != null && skill != null) {
      data.setPointsInSkill(skill, amount);
      ModTriggers.SKILL_POINT_ADDED.trigger((EntityPlayerMP) player, skill, amount);
    }

    return null;
  }
}
