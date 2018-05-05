package net.silentchaos512.borderblocks.util;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public class StatManager {

  public static final UUID ID_HEALTH = UUID.fromString("982a96da-8110-4813-96ee-a101828307cb");
  public static final UUID ID_DAMAGE = UUID.fromString("a3b6d1b6-ed85-4dc2-b854-b8133d741f52");
  public static final String NAME_HEALTH = Borderblocks.MOD_ID + ".HealthModifier";
  public static final String NAME_DAMAGE = Borderblocks.MOD_ID + ".DamageModifier";

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
    int multi = data.getLevel() - 1;

    // Health
    if (Config.PLAYER_HEALTH_PER_LEVEL > 0f) {
      IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
      if (attr != null) {
        float amount = Config.PLAYER_HEALTH_PER_LEVEL * multi;
        setModifier(attr, ID_HEALTH, NAME_HEALTH, amount, 0);
      }
    }

    // Attack Damage
    if (Config.PLAYER_DAMAGE_PER_LEVEL > 0f) {
      IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      if (attr != null) {
        float amount = Config.PLAYER_DAMAGE_PER_LEVEL * multi;
        setModifier(attr, ID_DAMAGE, NAME_DAMAGE, amount, 0);
      }
    }
  }
}
