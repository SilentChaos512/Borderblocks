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

package net.silentchaos512.borderblocks.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.client.gui.button.SkillButton;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.character.CharacterClass;
import net.silentchaos512.borderblocks.lib.character.SkillTreeBranch;
import net.silentchaos512.borderblocks.lib.character.SkillTreeBranch.SkillTier;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.lib.skill.action.ActionSkill;
import net.silentchaos512.borderblocks.network.MessageSkillRespec;
import net.silentchaos512.borderblocks.network.MessageSkillSetPoints;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.gui.TexturedButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiSkillTree extends GuiScreen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Borderblocks.MOD_ID, "textures/gui/skill_tree.png");
    private static final int SKILL_BUTTON_SPACING = 26;

    private int buttonId = 0;
    private GuiButton buttonRespec, buttonChangeClass, buttonResetXp;

    @Override
    public void initGui() {
        super.initGui();

        PlayerData data = PlayerDataHandler.get(mc.player);
        CharacterClass characterClass = data.getCharacterClass();

        String buttonText = Borderblocks.localization.getLocalizedString("gui", "respec");
        buttonRespec = new GuiButton(buttonId++, 5, 5, 80, 20, buttonText);
        buttonList.add(buttonRespec);
        buttonChangeClass = new GuiButton(buttonId++, 100, 5, 80, 20, "Change Class");
        buttonList.add(buttonChangeClass);
        buttonResetXp = new GuiButton(buttonId++, 300, 5, 80, 20, "Reset Level/XP");

        layoutActionSkill(120 + SKILL_BUTTON_SPACING, 36);
        layoutForBranch(characterClass.getBranchLeft(), 30, 70);
        layoutForBranch(characterClass.getBranchMiddle(), 120, 70);
        layoutForBranch(characterClass.getBranchRight(), 210, 70);
    }

    private void layoutActionSkill(int x, int y) {
        PlayerData data = PlayerDataHandler.get(mc.player);
        CharacterClass characterClass = data.getCharacterClass();
        Skill skill = characterClass.getActionSkill();
        int investedPoints = data.getPointsInSkill(skill);
        List<String> tooltip = wrapTooltipLines(skill.getTooltip(investedPoints, data));

        SkillButton button = new SkillButton(buttonId++, x, y, skill, tooltip);
        buttonList.add(button);
    }

    private void layoutForBranch(SkillTreeBranch branch, int startX, int startY) {
        for (int tier = 0; tier < branch.getTierCount(); ++tier) {
            SkillTier skillTier = branch.getTier(tier);
            int x = startX;
            int y = startY + SKILL_BUTTON_SPACING * tier;

            if (skillTier.getSkillCount() == 1)
                x += SKILL_BUTTON_SPACING;

            for (Skill skill : skillTier) {
                PlayerData data = PlayerDataHandler.get(Borderblocks.proxy.getClientPlayer());
                int investedPoints = data.getPointsInSkill(skill);
                List<String> tooltip = wrapTooltipLines(skill.getTooltip(investedPoints, data));

                SkillButton button = new SkillButton(buttonId++, x, y, skill, tooltip);
                buttonList.add(button);
                x += skillTier.getSkillCount() == 2 ? 2 * SKILL_BUTTON_SPACING : SKILL_BUTTON_SPACING;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        mc.renderEngine.bindTexture(TEXTURE);
        float f1 = 0.65f;
        float f2 = 0.25f;
        GlStateManager.color(f1, f2, f2);
        drawTexturedModalRect(26, 64, 172, 0, 83, 161);
        GlStateManager.color(f2, f1, f2);
        drawTexturedModalRect(116, 64, 172, 0, 83, 161);
        GlStateManager.color(f2, f2, f1);
        drawTexturedModalRect(206, 64, 172, 0, 83, 161);
        GlStateManager.color(1f, 1f, 1f);

        super.drawScreen(mouseX, mouseY, partialTicks);

        PlayerData data = PlayerDataHandler.get(mc.player);
        ProgressionTier tier = data != null ? data.getProgressionTier() : ProgressionTier.WOOD;
        ItemStack stack = tier.getRelicStack();
        this.itemRender.renderItemAndEffectIntoGUI(stack, 120 + 2 * SKILL_BUTTON_SPACING, 39);

        int skillPointsLeft = data.getAvailableSkillPoints();
        String line = Borderblocks.localization.getLocalizedString("gui", "skillPointsLeft", skillPointsLeft);
        int lineWidth = fontRenderer.getStringWidth(line);
        fontRenderer.drawStringWithShadow(line, 159 - lineWidth / 2, 232, 0xFFFFFF);

        // this.renderToolTip(stack, x, y);
        for (GuiButton button : this.buttonList) {
            if (button instanceof TexturedButton) {
                ((TexturedButton) button).drawHover(mouseX, mouseY);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        EntityPlayer player = Borderblocks.proxy.getClientPlayer();
        PlayerData data = PlayerDataHandler.get(player);

        if (button instanceof SkillButton) {
            Skill skill = ((SkillButton) button).getSkill();

            if (!(skill instanceof ActionSkill)) {
                // Which branch contains the skill, and is the tier accessible?
                SkillTreeBranch branch = data.getCharacterClass().getBranchOfSkill(skill);
                if (branch == null || branch.getTierOfSkill(skill) > branch.getHighestAccessibleTier(player)) {
                    // TODO: Feedback of some kind would be nice.
                    return;
                }
            }

            if (data.addPointToSkill(skill)) {
                int amount = data.getPointsInSkill(skill);
                // Update button tooltip.
                ((SkillButton) button).textList = wrapTooltipLines(skill.getTooltip(amount, data));

                Borderblocks.log.debug("actionPerformed: skill " + skill.getName() + " now has " + amount + " points invested");
                Borderblocks.network.wrapper.sendToServer(new MessageSkillSetPoints(player, skill, amount));
            }
        } else if (button == buttonRespec) {
            // Respec button
            data.respecSkills();
            Borderblocks.network.wrapper.sendToServer(new MessageSkillRespec(player));
            mc.displayGuiScreen(null);
        } else if (button == buttonChangeClass) {
            // Change class button
            mc.displayGuiScreen(new GuiChooseClass());
        } else if (button == buttonResetXp) {
            // Reset XP button (TODO: debug only?)
            data.setXp(0);
            data.respecSkills();
            Borderblocks.network.wrapper.sendToServer(new MessageSkillRespec(player));
        }
    }

    static final int LINE_WIDTH = 40;

    private List<String> wrapTooltipLines(List<String> list) {
        List<String> result = new ArrayList<>();
        // >:(
        for (String str : list) {
            String format = str.startsWith("\u00a7") ? str.substring(0, 2) : "";
            String[] split = new String[0];
            while (str.length() > LINE_WIDTH && split.length != 1) {
                split = splitLine(str);
                result.add(format + split[0]);
                if (split.length > 1)
                    str = split[1];
            }
            if (split.length != 1)
                result.add(format + str);
        }

        return result;
    }

    private String[] splitLine(String str) {
        if (str.length() <= LINE_WIDTH)
            return new String[]{str};
        for (int c = LINE_WIDTH; c < str.length(); ++c) {
            if (Character.isWhitespace(str.charAt(c))) {
                return new String[]{str.substring(0, c), str.substring(c + 1)};
            }
        }
        return new String[]{str};
    }
}
