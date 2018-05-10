package net.silentchaos512.borderblocks.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.command.CommandBaseSL;

public class CommandLevel extends CommandBaseSL {

  @Override
  public String getName() {

    return Borderblocks.MOD_ID + "_level";
  }

  @Override
  public List<String> getAliases() {

    return Lists.newArrayList("bb_level");
  }

  @Override
  public String getUsage(ICommandSender sender) {

    return "Usage: /" + getName() + " [player] [value]";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    String playerName = args.length > 0 && !args[0].equals("@p") ? args[0] : sender.getName();
    EntityPlayer player = server.getPlayerList().getPlayerByUsername(playerName);

    if (player == null) {
      tell(sender, "playerNotFound", true);
      return;
    }

    PlayerData data = PlayerDataHandler.get(player);

    // Get player level
    if (args.length < 2) {
      tell(sender, "playerLevel", true, data.getLevel());
      return;
    }

    // Set player level
    try {
      int levelToSet = parseInt(args[1]);
      if (levelToSet < 1 || levelToSet > Config.PLAYER_MAX_LEVEL) {
        tell(sender, "invalidLevel", true, 1, Config.PLAYER_MAX_LEVEL);
        return;
      }
      data.setLevelDirectly(levelToSet, true);
      tell(sender, "playerLevelSet", true, levelToSet);
    } catch (NumberFormatException ex) {
      tell(sender, ex.getMessage(), true);
    }
  }

  @Override
  public boolean isUsernameIndex(String[] args, int index) {

    return index == 2;
  }

  private void tell(ICommandSender sender, String key, boolean fromLocalizationFile, Object... args) {

    String locKey = "command." + Borderblocks.RESOURCE_PREFIX + key;
    String value = fromLocalizationFile
        ? Borderblocks.localization.getLocalizedString(locKey, args)
        : locKey;
    sender.sendMessage(new TextComponentString(value));
  }
}
