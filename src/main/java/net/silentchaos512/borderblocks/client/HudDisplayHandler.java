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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.client.key.KeyTracker;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.lib.skill.action.ActionSkill;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.TimeHelper;

public class HudDisplayHandler extends GuiScreen {
    private static final ResourceLocation HUD_TEXTURE = new ResourceLocation(Borderblocks.MOD_ID, "textures/gui/hud.png");
    private static final int VANILLA_XP_DISPLAY_TIME = TimeHelper.ticksFromSeconds(10);
    private static final int BORDERBLOCKS_LEVEL_COLOR = 0x7FFFFF;
    private static final int VANILLA_XP_LEVEL_COLOR = 8453920;

    private int lastVanillaLevel = -1;
    private int timerDisplayVanillaXp = 0;

    @SubscribeEvent
    public void renderTickPre(RenderGameOverlayEvent.Pre event) {
        if (mc == null)
            mc = Minecraft.getMinecraft();

        boolean typeXp = event.getType() == ElementType.EXPERIENCE;
        boolean typeJump = event.getType() == ElementType.JUMPBAR;
        if (typeXp || typeJump) {
            renderCooldown(event);
            if (typeXp)
                renderXp(event);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(ICONS);
            // dank/null crashes if I do this...
            // But then certain mods don't get the event they need... Gah!
//            if (typeXp && event.isCanceled()) {
//                MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(event, ElementType.EXPERIENCE));
//            }
        }
    }

    private void renderCooldown(RenderGameOverlayEvent.Pre event) {
        mc.renderEngine.bindTexture(HUD_TEXTURE);
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableBlend();

        PlayerData data = PlayerDataHandler.get(mc.player);
        if (data == null || data.getTotalSkillPoints() <= 0) return;

        ScaledResolution res = new ScaledResolution(mc);
        FontRenderer fontRender = mc.fontRenderer;
        boolean skillPointsAvailable = data.getAvailableSkillPoints() > 0;

        float posX = res.getScaledWidth() / 2 - 148;
        float posY = res.getScaledHeight() - 24;
        ActionSkill actionSkill = data.getCharacterClass().getActionSkill();
        boolean hasActionSkill = data.getPointsInSkill(actionSkill) > 0;
        int skillDuration = data.getActionSkillDuration();
        int skillDurationMax = TimeHelper.ticksFromSeconds(actionSkill.getSkillDuration(data));
        float cooldown = data.getCooldown();
        float cooldownMax = actionSkill.getCooldownTime();
        float fill = hasActionSkill ? 1f - (cooldown / cooldownMax) : 0f;

        // Background
        float bgShade = fill == 1f ? 0.15f : 0.25f;
        GlStateManager.color(bgShade, bgShade, bgShade, 0.75f);
        drawTexturedModalRect(posX, posY, 22, 0, 22, 22);
        // Frame
        if (skillDuration > 0) {
            // Skill is currently active
            fill = (float) skillDuration / skillDurationMax;
            int split = (int) (22 * fill);
            GlStateManager.color(bgShade / 2f, bgShade / 2f, bgShade, 0.6f);
            drawTexturedModalRect(posX, posY, 0, 0, 22, 22 - split);
            GlStateManager.color(0f, 0.75f, 1.0f, 0.6f);
            drawTexturedModalRect(posX, posY + 22 - split, 0, 22 - split, 22, split);
        } else if (cooldown > 0f || !hasActionSkill) {
            // Partially charged or action skill not unlocked
            int split = (int) (22 * fill);
            GlStateManager.color(bgShade, bgShade, skillPointsAvailable ? 0f : bgShade, 0.6f);
            drawTexturedModalRect(posX, posY, 0, 0, 22, 22 - split);
            GlStateManager.color(0.75f, 0.75f, skillPointsAvailable ? 0f : 0.75f, 0.6f);
            drawTexturedModalRect(posX, posY + 22 - split, 0, 22 - split, 22, split);
        } else {
            // Fully charged!
            float red = skillPointsAvailable ? 1f : 0f;
            float green = 1f;
            float blue = skillPointsAvailable || fill == 1f ? 0f : 1f;
            GlStateManager.color(red, green, blue, 0.8f);
            drawTexturedModalRect(posX, posY, 0, 0, 22, 22);
        }
        // Skill icon
        GlStateManager.color(1f, 1f, 1f, 0.75f);
        int iconIndex = data.getCharacterClass().getActionSkill().getTextureIndex() & 0xF;
        drawTexturedModalRect(posX + 3, posY + 3, 16 * iconIndex, 22, 16, 16);

        if (KeyTracker.isAltDown()) {
            GlStateManager.pushMatrix();
            float scale = 0.7f;
            GlStateManager.scale(scale, scale, 1f);
            fontRender.drawStringWithShadow("Alt", (posX + 10) / scale, (posY + 14) / scale, 0x7777FF);
            GlStateManager.popMatrix();
        }

        // Skill points
        if (skillPointsAvailable) {
            float scale = 0.75f;
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 1f);
            String text = "" + data.getAvailableSkillPoints();
            float x = (posX - fontRender.getStringWidth(text)) / scale;
            float y = (posY + 15.5f) / scale;
            fontRender.drawStringWithShadow(text, x, y, 0xFFFF00);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    private void renderXp(RenderGameOverlayEvent.Pre event) {
        PlayerData data = PlayerDataHandler.get(mc.player);
        if (data == null) return;

        ScaledResolution res = new ScaledResolution(mc);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        // Display vanilla XP level occasionally...
        int newVanillaLevel = mc.player.experienceLevel;
        if (lastVanillaLevel > -1 && newVanillaLevel > 0 && lastVanillaLevel != newVanillaLevel && newVanillaLevel % 5 == 0) {
            timerDisplayVanillaXp = VANILLA_XP_DISPLAY_TIME;
        }
        lastVanillaLevel = newVanillaLevel;
        if (timerDisplayVanillaXp > 0 || !Config.OVERRIDE_XP_BAR) {
            // Displaying vanilla XP bar. Draw player's character level to the left of the bar.
            drawSideLevel(mc.fontRenderer, width, height, data.getLevel(), BORDERBLOCKS_LEVEL_COLOR);
            return;
        }

        // Displaying Borderblocks XP bar. Cancel drawing of vanilla XP bar.
        event.setCanceled(true);

        mc.renderEngine.bindTexture(HUD_TEXTURE);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();

        FontRenderer fontrenderer = mc.fontRenderer;

        if (mc.playerController.gameIsSurvivalOrAdventure()) {
            int currentLevelXp = data.getXpForLevel(data.getLevel());
            int xpForNext = data.getXpForNextLevel() - currentLevelXp;
            int left = width / 2 - 91;

            if (xpForNext > 0) {
                short barWidth = 182;
                float xp = data.getXp() - currentLevelXp;
                int filled = (int) (xp / xpForNext * (float) (barWidth + 1));
                if (data.isMaxLevel())
                    filled = 0;
                int top = height - 32 + 3;
                drawTexturedModalRect(left, top, 0, 64, barWidth, 5);

                if (filled > 0) {
                    drawTexturedModalRect(left, top, 0, 69, filled, 5);
                }
            }

            if (mc.playerController.gameIsSurvivalOrAdventure()) {
                int color = 0x7FFFFF;
                String text = "" + data.getLevel();
                int x = (width - fontrenderer.getStringWidth(text)) / 2;
                int y = height - 31 - 4;
                fontrenderer.drawString(text, x + 1, y, 0);
                fontrenderer.drawString(text, x - 1, y, 0);
                fontrenderer.drawString(text, x, y + 1, 0);
                fontrenderer.drawString(text, x, y - 1, 0);
                fontrenderer.drawString(text, x, y, color);
            }

            // Draw vanilla xp level
            drawSideLevel(fontrenderer, width, height, mc.player.experienceLevel, VANILLA_XP_LEVEL_COLOR);
        }
        GlStateManager.enableBlend();
    }

    /**
     * Draws a number to the left of the XP bar
     */
    private void drawSideLevel(FontRenderer fontrenderer, int width, int height, int level, int color) {
        GlStateManager.pushMatrix();
        float scale = 0.5f;
        GlStateManager.scale(scale, scale, 1f);
        String text2 = "" + level;
        int textWidth = fontrenderer.getStringWidth(text2);
        int px = (int) ((width / 2 - 92 - textWidth * scale) / scale);
        int py = (int) ((height - 29) / scale);
        fontrenderer.drawString(text2, px, py, color);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase == Phase.END && timerDisplayVanillaXp > 0 && !mc.isGamePaused())
            --timerDisplayVanillaXp;
    }
}
