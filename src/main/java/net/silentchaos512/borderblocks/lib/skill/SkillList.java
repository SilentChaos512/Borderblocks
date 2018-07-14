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

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.action.ActionSkill;
import net.silentchaos512.borderblocks.lib.skill.action.ActionSkillScavenger;
import net.silentchaos512.borderblocks.lib.skill.action.ActionSkillSiren;
import net.silentchaos512.borderblocks.lib.skill.action.ActionSkillTamer;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkillList {
    public static final List<Skill> ALL_SKILLS = new ArrayList<>();

    // Action Skills
    public static final ActionSkill ACTION_UNDEFINED = new ActionSkill(Borderblocks.RESOURCE_PREFIX + "action_undefined") {

        @Override
        public float getCooldownTime() {
            return 10f;
        }

        @Override
        public boolean activate(EntityPlayer player, ProgressionTier tier, BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown) {
            return false;
        }

        @Override
        public float getSkillDuration(PlayerData data) {
            return 10f;
        }
    };
    public static final ActionSkillScavenger ACTION_SCAVENGER = new ActionSkillScavenger(Borderblocks.RESOURCE_PREFIX + "action_scavenger")
            .setTextureIndex(0xF0);
    public static final ActionSkillSiren ACTION_SIREN = new ActionSkillSiren(Borderblocks.RESOURCE_PREFIX + "action_siren")
            .setTextureIndex(0xF1);
    public static final ActionSkillTamer ACTION_TAMER = new ActionSkillTamer(Borderblocks.RESOURCE_PREFIX + "action_tamer")
            .setTextureIndex(0xF2);

    // Other skills sorted by type, then by name.
    public static final BlockDropsSkill FORAGER = new BlockDropsSkill(Borderblocks.RESOURCE_PREFIX + "forager",
            1, true, 0.05f).setTextureIndex(0x8);

    public static final BreakSpeedSkill BREAK_SPEED_GENERIC = new BreakSpeedSkill(Borderblocks.RESOURCE_PREFIX + "break_speed_generic",
            5, 0.2f, false).setTextureIndex(0x7);
    public static final BreakSpeedSkill BREAK_SPEED_LOGS = new BreakSpeedSkill(Borderblocks.RESOURCE_PREFIX + "break_speed_logs",
            5, 1.5f, true).setTextureIndex(0x4);
    public static final BreakSpeedSkill BREAK_SPEED_ORES = new BreakSpeedSkill(Borderblocks.RESOURCE_PREFIX + "break_speed_ores",
            5, 1.5f, true).setTextureIndex(0x2);

    public static final CooldownBonusSkill COOLDOWN_SCAV = new CooldownBonusSkill(Borderblocks.RESOURCE_PREFIX + "cooldown_scavenger",
            5, 0.07f).setTextureIndex(0x1);
    public static final CooldownBonusSkill COOLDOWN_SIREN = new CooldownBonusSkill(Borderblocks.RESOURCE_PREFIX + "cooldown_siren",
            5, 0.06f);

    public static final ChanceActivationSkill ARROW_DEFLECTION = new ChanceActivationSkill(Borderblocks.RESOURCE_PREFIX + "arrow_deflection",
            5, 0.15f).setTextureIndex(0x0);

    public static final DamageReductionSkill FALL_DAMAGE_DOWN = new DamageReductionSkill(Borderblocks.RESOURCE_PREFIX + "fall_damage_down",
            5, 0.16f, DamageSource.FALL).setTextureIndex(0xF);
    public static final DamageReductionSkill FALL_DAMAGE_DOWN_SCAV = new DamageReductionSkill(Borderblocks.RESOURCE_PREFIX + "fall_damage_down_scavenger",
            5, 5, new DamageSource[0]).setTextureIndex(0xF);
    public static final DamageReductionSkill FIRE_DAMAGE_DOWN = new DamageReductionSkill(Borderblocks.RESOURCE_PREFIX + "fire_damage_down",
            5, 0.15f, DamageSource.IN_FIRE, DamageSource.ON_FIRE, DamageSource.LAVA);

    public static final DurationSkill DURATION_BOOST_SCAV = new DurationSkill(Borderblocks.RESOURCE_PREFIX + "duration_boost_scavenger",
            5, 12f).setTextureIndex(0x9);
    public static final DurationSkill DURATION_BOOST_SIREN = new DurationSkill(Borderblocks.RESOURCE_PREFIX + "duration_boost_siren",
            5, 2f).setTextureIndex(0x0);

    public static final ModifierSkill ARMOR_BOOST = new ModifierSkill(Borderblocks.RESOURCE_PREFIX + "armor_boost",
            5, SharedMonsterAttributes.ARMOR, 1.2, 0).setTextureIndex(0xD);
    public static final ModifierSkill ARMOR_BOOST_TAMER = new ModifierSkill(Borderblocks.RESOURCE_PREFIX + "armor_boost_tamer",
            5, SharedMonsterAttributes.ARMOR, 0.02, 1).setTextureIndex(0xD);
    public static final ModifierSkill ATTACK_SPEED_BOOST_K = new ModifierSkill(Borderblocks.RESOURCE_PREFIX + "attack_speed_boost",
            5, SharedMonsterAttributes.ATTACK_SPEED, 0.6f, 0).setKillSkill(10f).setTextureIndex(0x0);
    public static final ModifierSkill HEALTH_BOOST_SCAV = new ModifierSkill(Borderblocks.RESOURCE_PREFIX + "health_boost_scavenger",
            5, SharedMonsterAttributes.MAX_HEALTH, 0.06, 1).setTextureIndex(0xB);
    public static final ModifierSkill HEALTH_BOOST_SIREN = new ModifierSkill(Borderblocks.RESOURCE_PREFIX + "health_boost_siren",
            5, SharedMonsterAttributes.MAX_HEALTH, 0.04, 1).setTextureIndex(0xB);
    public static final ModifierSkill LUCK_BOOST = new ModifierSkill(Borderblocks.RESOURCE_PREFIX + "luck_boost",
            5, SharedMonsterAttributes.LUCK, 0.6, 0).setTextureIndex(0xA);
    public static final ModifierSkill SPEED_BOOST = new ModifierSkill(Borderblocks.RESOURCE_PREFIX + "speed_boost",
            5, SharedMonsterAttributes.MOVEMENT_SPEED, 0.12, 1).setTextureIndex(0xF);

    public static final RegenSkill REGEN_SIREN = new RegenSkill(Borderblocks.RESOURCE_PREFIX + "regen_siren",
            5, 0.004f).setTextureIndex(0x0);

    public static final Skill BARRIER_SUFFOCATE = new Skill(Borderblocks.RESOURCE_PREFIX + "barrier_suffocate",
            5).setTextureIndex(0x0);
    public static final Skill BARRIER_TELEPORT = new Skill(Borderblocks.RESOURCE_PREFIX + "barrier_teleport",
            1).setTextureIndex(0x0);
    public static final Skill IGNITION = new Skill(Borderblocks.RESOURCE_PREFIX + "ignition",
            1).setTextureIndex(0x0);
    // FIXME: fortune and silk touch will conflict!
    public static final Skill MULTI_TOOL_FORTUNE = new Skill(Borderblocks.RESOURCE_PREFIX + "multi_tool_fortune",
            5).setTextureIndex(0x11);
    public static final Skill MULTI_TOOL_SILKTOUCH = new Skill(Borderblocks.RESOURCE_PREFIX + "multi_tool_silktouch",
            5).setTextureIndex(0x12);
    public static final Skill MULTI_TOOL_REPAIR = new Skill(Borderblocks.RESOURCE_PREFIX + "multi_tool_repair",
            1).setTextureIndex(0x10).setModifierValue(SkillConst.MULTI_TOOL_REPAIR_AMOUNT);
    public static final Skill PET_HEALTH_UP = new Skill(Borderblocks.RESOURCE_PREFIX + "pet_health_boost",
            5).setTextureIndex(0x0).setModifierValue(0.1f, true);
    public static final Skill PHANTOM_MOUNT_HEALTH_UP = new Skill(Borderblocks.RESOURCE_PREFIX + "phantom_mount_health_boost",
            5).setTextureIndex(0x0).setModifierValue(0.1f, true);
    public static final Skill PHANTOM_MOUNT_JUMP_UP = new Skill(Borderblocks.RESOURCE_PREFIX + "phantom_mount_jump_boost",
            5).setTextureIndex(0x0).setModifierValue(0.2f, true);
    public static final Skill PHANTOM_MOUNT_SPEED_UP = new Skill(Borderblocks.RESOURCE_PREFIX + "phantom_mount_speed_boost",
            5).setTextureIndex(0x0).setModifierValue(0.1f, true);

    // Dummy skills
    public static final Skill PLACEHOLDER = new Skill(Borderblocks.RESOURCE_PREFIX + "placeholder", 5).setTextureIndex(0xFF);

    @Nullable
    public static Skill getByName(String name) {
        for (Skill skill : ALL_SKILLS)
            if (skill.getName().equals(name))
                return skill;
        return null;
    }
}
