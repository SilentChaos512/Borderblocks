package net.silentchaos512.borderblocks.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.borderblocks.Borderblocks;

public class ModDamageSource extends DamageSource {

  // TODO: Elemental damage types? How would that best be handled?

  public ModDamageSource(String damageTypeIn) {

    super(Borderblocks.RESOURCE_PREFIX + damageTypeIn);
  }

  @Override
  public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {

    EntityLivingBase entitylivingbase = entityLivingBaseIn.getAttackingEntity();
    String s = "death.attack." + this.damageType;
    String s1 = s + ".player";
    return entitylivingbase != null
        ? new TextComponentTranslation(s1, entityLivingBaseIn.getDisplayName(), entitylivingbase.getDisplayName())
        : new TextComponentTranslation(s, entityLivingBaseIn.getDisplayName());
  }
}
