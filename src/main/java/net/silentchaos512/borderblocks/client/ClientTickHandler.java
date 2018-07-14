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

package net.silentchaos512.borderblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;

import java.util.ArrayDeque;
import java.util.Queue;

public class ClientTickHandler {
    public static volatile Queue<Runnable> scheduledActions = new ArrayDeque<>();

    public static int ticksInGame = 0;
    public static float partialTicks = 0f;
    public static float delta = 0f;
    public static float total = 0f;

    private void calcDelta() {
        float oldTotal = total;
        total = ticksInGame + partialTicks;
        delta = total - oldTotal;
    }

    @SubscribeEvent
    public void clientTickEnd(ClientTickEvent event) {
        if (event.phase == Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.world == null) {
                PlayerDataHandler.cleanup();
            } else if (mc.player != null) {
                while (!scheduledActions.isEmpty()) {
                    scheduledActions.poll().run();
                }
            }

            GuiScreen gui = mc.currentScreen;
            if (gui == null || !gui.doesGuiPauseGame()) {
                ++ticksInGame;
                partialTicks = 0;
            }

            calcDelta();
        }
    }
}
