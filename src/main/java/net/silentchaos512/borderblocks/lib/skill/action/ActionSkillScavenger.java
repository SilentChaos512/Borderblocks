package net.silentchaos512.borderblocks.lib.skill.action;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.init.ModItems;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.PlayerHelper;
import net.silentchaos512.lib.util.StackHelper;
import net.silentchaos512.lib.util.TimeHelper;

public class ActionSkillScavenger extends ActionSkill {

  public ActionSkillScavenger(String name) {

    super(name);
  }

  @Override
  public boolean activate(EntityPlayer player, ProgressionTier tier, BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown) {

    if (StackHelper.isValid(player.getHeldItemMainhand())) {
      ChatHelper.sendStatusMessage(player, Borderblocks.localization.getLocalizedString("skill." + name + ".needEmptyHand"), true);
      return false;
    }

    PlayerData data = PlayerDataHandler.get(player);
    int duration = TimeHelper.ticksFromSeconds(getSkillDuration(data));
    ItemStack stack = ModItems.scavMultiTool.create(player, tier, duration);
    PlayerHelper.giveItem(player, stack);

    return true;
  }

  @Override
  public float getCooldownTime() {

    // TODO: Add config?
    return 120f;
  }

  @Override
  public float getSkillDuration(PlayerData playerData) {

    int points = playerData.getPointsInSkill(SkillList.DURATION_BOOST_SCAV);
    return 60f + points * SkillList.DURATION_BOOST_SCAV.getIncreasePerPoint();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<String> getTooltip(int investedPoints, PlayerData playerData) {

    ProgressionTier tier = playerData.getProgressionTier();
    List<String> list = new ArrayList<>();
    LocalizationHelper loc = Borderblocks.localization;
    list.add(TextFormatting.GOLD + loc.getLocalizedString("skill." + name + ".name"));
    list.add("");
    list.add(loc.getLocalizedString("skill." + name + ".desc1"));
    list.add("");
    list.add(loc.getLocalizedString("skill." + name + ".desc2",
        (int) getSkillDuration(playerData), (int) getCooldownTime(), tier.ordinal()));
    list.add("");
    list.add(loc.getLocalizedString("skill", "action.progressionTier", tier.getFormattedName()));
    list.add(String.format("Points: %d / %d", investedPoints, maxPoints));
    return list;
  }
}
