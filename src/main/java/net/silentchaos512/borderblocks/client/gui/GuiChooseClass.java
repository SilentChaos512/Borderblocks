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
import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.client.gui.button.CharacterClassButton;
import net.silentchaos512.borderblocks.lib.character.CharacterClass;
import net.silentchaos512.borderblocks.network.MessageSelectClass;
import net.silentchaos512.borderblocks.network.MessageSkillRespec;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.gui.TexturedButton;
import net.silentchaos512.lib.util.LocalizationHelper;

import java.io.IOException;

public class GuiChooseClass extends GuiScreen {
    private int buttonId = 0;
    private GuiButton buttonCancel;

    @Override
    public void initGui() {
        super.initGui();
        LocalizationHelper loc = Borderblocks.localization;

        int y = 30;
        for (CharacterClass charClass : CharacterClass.ALL_CLASSES) {
            GuiButton button = new CharacterClassButton(buttonId++, 5, y, 100, 20, charClass);
            buttonList.add(button);
            y += 30;
        }

        y += 60;
        String buttonText = loc.getLocalizedString("gui", "cancel");
        buttonCancel = new GuiButton(1000, 5, y, 100, 20, buttonText);
        buttonList.add(buttonCancel);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        mc.fontRenderer.drawStringWithShadow("Choose a Class", 5, 5, 0xFFFFFF);

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

        if (button instanceof CharacterClassButton) {
            CharacterClass charClass = ((CharacterClassButton) button).getCharacterClass();
            CharacterClass currentClass = data.getCharacterClass();

            data.setCharacterClass(charClass);
            Borderblocks.network.wrapper.sendToServer(new MessageSelectClass(player, charClass));

            if (charClass != currentClass) {
                // Also do a respec to refund skill points.
                data.respecSkills();
                Borderblocks.network.wrapper.sendToServer(new MessageSkillRespec(player));
            }
            mc.displayGuiScreen(null);
        } else if (button == buttonCancel) {
            mc.displayGuiScreen(null);
        }
    }
}
