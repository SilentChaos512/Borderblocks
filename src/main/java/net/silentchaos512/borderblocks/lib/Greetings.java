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

package net.silentchaos512.borderblocks.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.lib.util.ChatHelper;

import java.util.ArrayList;
import java.util.List;

public class Greetings {

    private static boolean DO_PRERELEASE_GREETING = true;
    private static final String PREFIX = "[" + Borderblocks.MOD_NAME + "] ";

    private static List<String> extraMessages = new ArrayList<>();

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
