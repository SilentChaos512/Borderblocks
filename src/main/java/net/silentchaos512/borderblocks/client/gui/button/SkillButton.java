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

package net.silentchaos512.borderblocks.client.gui.button;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.silentchaos512.borderblocks.client.gui.GuiSkillTree;
import net.silentchaos512.borderblocks.lib.character.SkillTreeBranch;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.gui.TexturedButton;

import java.util.List;

public class SkillButton extends TexturedButton {

    @Getter(value = AccessLevel.PUBLIC)
    protected Skill skill;

    public SkillButton(int id, int x, int y, Skill skill) {
        super(skill.getTexture(), id, x, y, getTexturePosX(skill), getTexturePosY(skill), 22, 22);
        this.skill = skill;
    }

    public SkillButton(int id, int x, int y, Skill skill, List hoverTextList) {
        super(skill.getTexture(), id, x, y, getTexturePosX(skill), getTexturePosY(skill), 22, 22, hoverTextList);
        this.skill = skill;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        PlayerData data = PlayerDataHandler.get(minecraft.player);

        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
                    && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            if (k == 0) {
                k = 1;
            }
            // TODO: Change color based on hover state?
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);

            int points = data.getPointsInSkill(skill);
            int maxPoints = skill.getMaxPoints();
            SkillTreeBranch branch = data.getCharacterClass().getBranchOfSkill(skill);
            int tierOfSkill = branch == null ? -1 : branch.getTierOfSkill(skill);
            int currentTier = branch == null ? -1 : branch.getHighestAccessibleTier(minecraft.player);

            // Draw frame (22x22)
            Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSkillTree.TEXTURE);
            if (tierOfSkill > currentTier)
                GlStateManager.color(0.3F, 0.3F, 0.3F, 1.0F);
            drawTexturedModalRect(x, y, 0, 0, 22, 22);
            if (points == 0)
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            else if (points < maxPoints)
                GlStateManager.color(1.0F, 0.5F, 0.0F, 1.0F);
            else
                GlStateManager.color(0.0F, 1.0F, 0.0F, 1.0F);

            if (points > 0)
                drawTexturedModalRect(x, y, 0, 0, 22, 22 * points / maxPoints);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            // Draw skill icon
            if (tierOfSkill > currentTier)
                GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
            minecraft.getTextureManager().bindTexture(this.resLoc);
            drawTexturedModalRect(x + 3, y + 3, texturePosX, texturePosY, width - 6, height - 6);
            this.mouseDragged(minecraft, mouseX, mouseY);
        }
    }

    static int getTexturePosX(Skill skill) {
        return 16 * (skill.getTextureIndex() & 0xF);
    }

    static int getTexturePosY(Skill skill) {
        return 16 * (skill.getTextureIndex() >> 4);
    }
}
