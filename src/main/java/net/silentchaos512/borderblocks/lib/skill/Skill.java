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

package net.silentchaos512.borderblocks.lib.skill;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class Skill {
    private static final ResourceLocation TEXTURE_SKILLS = new ResourceLocation(Borderblocks.MOD_ID, "textures/gui/skills.png");

    @Getter(value = AccessLevel.PUBLIC)
    protected ResourceLocation name;
    @Getter(value = AccessLevel.PUBLIC)
    protected int maxPoints;

    @Getter(value = AccessLevel.PUBLIC)
    protected boolean isKillSkill = false;
    @Getter(value = AccessLevel.PUBLIC)
    protected float killSkillDuration = 0f;

    private ResourceLocation texture;
    private int textureIndex = 0;

    /**
     * Added in 0.1.1, mostly for desc2.
     */
    @Getter(value = AccessLevel.PUBLIC)
    protected float modifierAmount = 1f;
    /**
     * Added in 0.1.3, again used for desc2.
     */
    protected boolean displayModifierAsPercentage = false;

    public Skill(ResourceLocation name, int maxPoints) {
        this.name = name;
        this.maxPoints = maxPoints;

        if (maxPoints <= 0)
            throw new IllegalArgumentException("maxPoints must be greater than zero.");

        this.texture = TEXTURE_SKILLS;

        SkillList.ALL_SKILLS.add(this);
    }

    public void trigger(EntityLivingBase entity, int pointsInSkill) {
    }

    public void applyTo(EntityPlayer player, int pointsInSkill) {
    }

    public void removeFrom(EntityPlayer player) {
    }

    @SideOnly(Side.CLIENT)
    public List<String> getTooltip(int investedPoints, PlayerData playerData) {
        List<String> list = new ArrayList<>();
        list.add(TextFormatting.GOLD + Borderblocks.i18n.translate("skill." + name + ".name"));
        list.add("");
        list.add(Borderblocks.i18n.translate("skill." + name + ".desc1"));
        list.add("");

        if (investedPoints > 0) {
            list.add(getDesc2(investedPoints));
            list.add("");
        }
        if (investedPoints < maxPoints) {
            list.add(Borderblocks.i18n.miscText("nextLevel"));
            list.add(getDesc2(investedPoints + 1));
            list.add("");
        }

        list.add(String.format("Points: %d / %d", investedPoints, maxPoints));
        return list;
    }

    public <T extends Skill> T setKillSkill(float duration) {
        this.isKillSkill = duration > 0f;
        this.killSkillDuration = duration;
        return (T) this;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getTexture() {
        return TEXTURE_SKILLS;
    }

    public <T extends Skill> T setTexture(ResourceLocation texture) {
        this.texture = texture;
        return (T) this;
    }

    @SideOnly(Side.CLIENT)
    public int getTextureIndex() {
        return textureIndex;
    }

    public <T extends Skill> T setTextureIndex(int index) {
        this.textureIndex = index;
        return (T) this;
    }

    public <T extends Skill> T setModifierValue(float amount) {
        this.modifierAmount = amount;
        return (T) this;
    }

    public <T extends Skill> T setModifierValue(float amount, boolean displayAsPercentage) {
        this.modifierAmount = amount;
        this.displayModifierAsPercentage = displayAsPercentage;
        return (T) this;
    }

    protected String getDesc2(int investedPoints) {
        return TextFormatting.AQUA + Borderblocks.i18n.translate("skill." + name + ".desc2", getDesc2Params(investedPoints));
    }

    protected Object[] getDesc2Params(int investedPoints) {
        if (displayModifierAsPercentage) {
            int val = (int) (100 * investedPoints * modifierAmount);
            return new Object[]{val};
        }

        float val = investedPoints * modifierAmount;
        if (val == (int) val)
            return new Object[]{(int) val};
        else
            return new Object[]{val};
    }
}
