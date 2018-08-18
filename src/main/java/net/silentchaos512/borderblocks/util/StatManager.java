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

package net.silentchaos512.borderblocks.util;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

import java.util.UUID;

public class StatManager {

    private static final UUID ID_HEALTH = UUID.fromString("982a96da-8110-4813-96ee-a101828307cb");
    private static final UUID ID_DAMAGE = UUID.fromString("a3b6d1b6-ed85-4dc2-b854-b8133d741f52");
    private static final String NAME_HEALTH = Borderblocks.MOD_ID + ".HealthModifier";
    private static final String NAME_DAMAGE = Borderblocks.MOD_ID + ".DamageModifier";

    public static void setModifier(IAttributeInstance attr, UUID id, String name, double amount, int op) {
        if (attr == null)
            return;

        // Get current and new modifier.
        AttributeModifier mod = attr.getModifier(id);
        AttributeModifier newMod = new AttributeModifier(id, name, amount, op);

        // Remove the old, apply the new.
        if (mod != null)
            attr.removeModifier(mod);
        attr.applyModifier(newMod);
    }

    public static void setPlayerStats(EntityPlayer player) {
        PlayerData data = PlayerDataHandler.get(player);
        if (data == null) return;
        int multi = data.getLevel() - 1;

        // Health
        if (Config.PLAYER_HEALTH_PER_LEVEL > 0f) {
            IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            float amount = Config.PLAYER_HEALTH_PER_LEVEL * multi;
            setModifier(attr, ID_HEALTH, NAME_HEALTH, amount, 0);
        }

        // Attack Damage
        if (Config.PLAYER_DAMAGE_PER_LEVEL > 0f) {
            IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            float amount = Config.PLAYER_DAMAGE_PER_LEVEL * multi;
            setModifier(attr, ID_DAMAGE, NAME_DAMAGE, amount, 0);
        }
    }
}
