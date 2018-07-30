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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.event.ClientTicks;
import org.lwjgl.opengl.GL11;

public class DebugDisplayHandler {

    @SubscribeEvent
    public void renderTick(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!Config.DEBUG_MODE || mc.world == null || event.getType() != ElementType.ALL || mc.isGamePaused()) {
            return;
        }

        ScaledResolution res = event.getResolution();
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        FontRenderer fontRender = mc.fontRenderer;

        GL11.glPushMatrix();
        float scale = 1.0f;

        String text = getDebugText();
        int y = 3;
        for (String line : text.split("\n")) {
            String[] array = line.split("=");
            if (array.length == 2) {
                fontRender.drawStringWithShadow(array[0].trim(), 3, y, 0xFFFFFF);
                fontRender.drawStringWithShadow(array[1].trim(), 90, y, 0xFFFFFF);
            } else {
                fontRender.drawStringWithShadow(line, 3, y, 0xFFFFFF);
            }
            y += 10;
        }

        GL11.glPopMatrix();

        // renderTestString(width, height, fontRender);
    }

    private String getDebugText() {
        World world = Minecraft.getMinecraft().world;
        EntityPlayer player = Minecraft.getMinecraft().player;
        PlayerData data = PlayerDataHandler.get(player);
        if (data == null)
            return "Player data is null!";

        String ret = "";

        ret += String.format("Class=%s\n", data.getCharacterClass().getLocalizedName());
        ret += String.format("Level=%d\n", data.getLevel());
        ret += String.format("XP=%,d / %,d\n", data.getXp(), data.getXpForNextLevel());
        ret += String.format("Skill Count=%d\n", data.getSkills().size());
        ret += String.format("Cooldown=%.1f", data.getCooldown());

        return ret;
    }

    private void renderTestString(int width, int height, FontRenderer fontRender) {
        String test = "This is a test!";
        int testWidth = fontRender.getStringWidth(test);

        GL11.glPushMatrix();
        float delta = ClientTicks.ticksInGame / 20f;
        float testScale = 1.5f + MathHelper.sin(4 * delta) / 4;
        float testScaleY = 1.5f + 0.75f * MathHelper.sin(3 * delta);
        GlStateManager.translate(width / 2, height / 4, 0);
        // Periodically scaling/rotating
        GlStateManager.scale(testScale, testScaleY, 1f);
        GlStateManager.rotate(20 * MathHelper.sin(2 * delta), 0, 0, 1);
        // Skewed text
        // GlStateManager.rotate(7, 0, 0, 1);
        // GlStateManager.scale(1.3f, 1.5f, 1f);
        fontRender.drawStringWithShadow(test, -testWidth / 2, -fontRender.FONT_HEIGHT / 2, 0xFFFF00);
        GL11.glPopMatrix();
    }
}
