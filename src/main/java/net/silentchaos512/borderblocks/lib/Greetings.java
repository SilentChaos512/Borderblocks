package net.silentchaos512.borderblocks.lib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.lib.util.ChatHelper;

public class Greetings {

  static boolean DO_PRERELEASE_GREETING = true;
  static final String PREFIX = "[" + Borderblocks.MOD_NAME + "] ";

  static List<String> extraMessages = new ArrayList<>();

  /**
   * Adds messages to the player's chat log. Use addExtraMessage to add messages to the list.
   */
  public static void greetPlayer(EntityPlayer player) {

    if (DO_PRERELEASE_GREETING)
      doPrereleaseGreeting(player);

    for (String str : extraMessages)
      ChatHelper.sendMessage(player, PREFIX + str);
  }

  public static void doPrereleaseGreeting(EntityPlayer player) {

    String line = "ALPHA: Thank you for trying Borderblocks! Please remember the mod is very"
        + " incomplete. Make backups of your world! Report issues on the GitHub issue tracker.";
    line = PREFIX + line;
    line = line.replaceAll("&", "\u00a7");
    ChatHelper.sendMessage(player, TextFormatting.RED + line);
  }

  /**
   * Add an additional message to display when the player logs in to a world.
   */
  public static void addExtraMessage(String str) {

    extraMessages.add(str);
  }
}
