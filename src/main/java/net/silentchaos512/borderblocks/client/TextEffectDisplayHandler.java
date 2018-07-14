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
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.silentchaos512.borderblocks.Borderblocks;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class TextEffectDisplayHandler {
    public static TextEffectDisplayHandler INSTANCE = new TextEffectDisplayHandler();

    private List<TextEffect> effectList = new ArrayList<>();

    public void addEffect(TextEffect effect) {
        effectList.add(effect);
    }

    public void addXpEffect(int amount) {
        String text = Borderblocks.localization.getMiscText("xpEarned", amount);
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int startX = res.getScaledWidth() / 2;
        int startY = res.getScaledHeight() - 100;
        int endX = startX;
        int endY = res.getScaledHeight() - 20;
        int fadeTime = 10;
        // TODO: Different colors for different XP sources?
        addEffect(new TextEffect(text, 0xFFFFFF, startX, startY, 40, 1.05f, 1.15f, fadeTime, endX, endY, fadeTime));
    }

    public void addLevelUpEffect() {
        String text = Borderblocks.localization.getMiscText("levelUp");
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int posX = res.getScaledWidth() / 2;
        int posY = res.getScaledHeight() / 3;
        addEffect(new TextEffect(text, 0xFFFF00, posX, posY, 80, 3.0f, 3.6f, 30));
    }

    @SubscribeEvent
    public void renderTick(RenderGameOverlayEvent.Post event) {
        if (event.getType() != ElementType.TEXT)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        for (TextEffect effect : effectList)
            effect.render(mc.fontRenderer, event.getPartialTicks());
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if (event.phase != Phase.END)
            return;

        for (TextEffect effect : effectList)
            effect.updateTick();
        effectList.removeIf(effect -> effect.life <= 0);
    }

    public static class TextEffect {
        String text;
        int color;
        float posX;
        float posY;
        int life;
        float scale;
        float scaleDelta;
        int fadeTime;
        float speedX;
        float speedY;
        int moveStartTime;

        public TextEffect(String text, int color, int posX, int posY, int life, float startScale, float endScale, int fadeTime) {
            this(text, color, posX, posY, life, startScale, endScale, fadeTime, posX, posY, 0);
        }

        public TextEffect(String text, int color, int startPosX, int startPosY, int life, float startScale, float endScale,
                          int fadeTime, int endPosX, int endPosY, int moveStartTime) {
            this.text = text;
            this.color = color;
            this.posX = startPosX;
            this.posY = startPosY;
            this.life = life;
            this.scale = startScale;
            this.scaleDelta = (endScale - startScale) / life;
            this.fadeTime = fadeTime;
            this.speedX = (float) (endPosX - startPosX) / moveStartTime;
            this.speedY = (float) (endPosY - startPosY) / moveStartTime;
            this.moveStartTime = moveStartTime;

            if (startPosX < 0 || startPosY < 0) {
                ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
                if (startPosX < 0)
                    this.posX = res.getScaledWidth() / 2;
                if (startPosY < 0)
                    this.posY = res.getScaledHeight() / 2;
            }
        }

        public void updateTick() {
            --life;
            scale += scaleDelta;
            if (shouldMove()) {
                posX += speedX;
                posY += speedY;
            }
        }

        private boolean shouldMove() {
            return life <= moveStartTime;
        }

        public void render(FontRenderer fontRender, float partialTicks) {
            GlStateManager.pushMatrix();

            int width = fontRender.getStringWidth(text);
            int height = fontRender.FONT_HEIGHT;
            float partialX = shouldMove() ? speedX * partialTicks : 0f;
            float partialY = shouldMove() ? speedY * partialTicks : 0f;
            float x = (posX + partialX - width * scale / 2f) / scale;
            float y = (posY + partialY - height * scale / 2f) / scale;

            int colorThisFrame = color;
            if (life <= fadeTime) {
                int alpha = 255 * life / fadeTime;
                colorThisFrame |= (alpha << 24);
            }

            GlStateManager.scale(scale, scale, 1f);
            GlStateManager.translate(x, y, 0);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            fontRender.drawStringWithShadow(text, 0, 0, colorThisFrame);

            GL11.glDisable(GL11.GL_BLEND);
            GlStateManager.popMatrix();
        }
    }
}
