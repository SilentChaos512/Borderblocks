package net.silentchaos512.borderblocks.event;

import java.util.Calendar;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.advancements.ClassChosenTrigger;
import net.silentchaos512.borderblocks.advancements.ModTriggers;
import net.silentchaos512.borderblocks.advancements.SkillPointAddedTrigger;
import net.silentchaos512.borderblocks.advancements.UseActionSkillTrigger;
import net.silentchaos512.borderblocks.advancements.UseItemTrigger;
import net.silentchaos512.borderblocks.lib.Greetings;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public class CommonEvents {

  @SubscribeEvent
  public void onPlayerJoinedServer(PlayerLoggedInEvent event) {

    // Sync player data and set health.
    if (event.player instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) event.player;
      PlayerData data = PlayerDataHandler.get(player);

      // Resets, based on config? (TODO: copied from Scaling Health, doing anything with this?)
      Calendar today = Calendar.getInstance();
      Calendar lastTimePlayed = data.getLastTimePlayed();

      data.getLastTimePlayed().setTime(today.getTime());

      // WTF? (add listener for advancement with custom criterion?)
      addListenerHack(player, ModTriggers.CLASS_CHOSEN, new ClassChosenTrigger.Instance("any"), "choose_class", "class_chosen");
      addListenerHack(player, ModTriggers.USE_ITEM, new UseItemTrigger.Instance(ItemPredicate.ANY), "progression_stone", "use_item");
      addListenerHack(player, ModTriggers.SKILL_POINT_ADDED, new SkillPointAddedTrigger.Instance("any", 1), "get_action_skill", "skill_point_added");
      addListenerHack(player, ModTriggers.USE_ACTION_SKILL, new UseActionSkillTrigger.Instance("any"), "use_action_skill", "use_action_skill");

      Greetings.greetPlayer(player);
    }
  }

  @SubscribeEvent
  public void onPlayerLeftServer(PlayerLoggedOutEvent event) {

    if (event.player instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) event.player;
      PlayerData data = PlayerDataHandler.get(player);

      // TODO: do we need to remove the hacky advancement listeners?
    }
  }

  /**
   * A hacky(?) solution for adding advancement listeners
   * 
   * @param player
   *          The EntityPlayerMP (needed)
   * @param trigger
   *          ModTriggers.(whatever) (probably needed)
   * @param instance
   *          CustomTrigger.Instance instance (probably needed? constructor argument does not seem to matter)
   * @param name
   *          The advancement name (only need to add listener for one advancement of this type? um, what?)
   * @param criterionName
   *          The criterion name (idk if this is needed)
   */
  private <T extends AbstractCriterionInstance> void addListenerHack(EntityPlayerMP player, ICriterionTrigger<T> trigger, T instance, String name, String criterionName) {

    ResourceLocation resource = new ResourceLocation(Borderblocks.RESOURCE_PREFIX + name);
    Advancement advancement = player.mcServer.getAdvancementManager().getAdvancement(resource);
    trigger.addListener(player.getAdvancements(), new ICriterionTrigger.Listener<T>(instance, advancement, criterionName));
  }
}
