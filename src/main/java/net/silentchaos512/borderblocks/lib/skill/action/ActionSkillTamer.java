package net.silentchaos512.borderblocks.lib.skill.action;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.silentchaos512.borderblocks.entity.PhantomHorse;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.TimeHelper;


public class ActionSkillTamer extends ActionSkill {

  public ActionSkillTamer(String name) {

    super(name);
  }

  @Override
  public boolean activate(EntityPlayer player, ProgressionTier tier, BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown) {

    PlayerData data = PlayerDataHandler.get(player);
    int duration = TimeHelper.ticksFromSeconds(getSkillDuration(data));

    PhantomHorse horse = new PhantomHorse(player.world, player, duration);
    horse.setPositionAndUpdate(player.posX, player.posY + 1, player.posZ);
    horse.setTamedBy(player);
    horse.setHorseSaddled(true);

    int healthBoostLevel = data.getPointsInSkill(SkillList.PHANTOM_MOUNT_HEALTH_UP);
    int jumpBoostLevel = data.getPointsInSkill(SkillList.PHANTOM_MOUNT_JUMP_UP);
    int speedBoostLevel = data.getPointsInSkill(SkillList.PHANTOM_MOUNT_SPEED_UP);

    player.world.spawnEntity(horse);
    return true;
  }

  @Override
  public float getCooldownTime() {

    return 5f; // TODO: 120?
  }

  @Override
  public float getSkillDuration(PlayerData data) {

   return 60f;
  }

}
