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

package net.silentchaos512.borderblocks.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.advancements.*;
import net.silentchaos512.borderblocks.item.CraftingItems;
import net.silentchaos512.borderblocks.lib.Greetings;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.borderblocks.util.StatManager;

import java.util.Calendar;

public class CommonEvents {

    @SubscribeEvent
    public void onPlayerJoinedServer(PlayerLoggedInEvent event) {
        // Sync player data.
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            PlayerData data = PlayerDataHandler.get(player);

            // Update last time joined (not really used for much, it was copied from Scaling Health)
            Calendar today = Calendar.getInstance();
            Calendar lastTimePlayed = data.getLastTimePlayed();
            data.getLastTimePlayed().setTime(today.getTime());

            // WTF? (add listener for advancement with custom criterion?)
            addListenerHack(player, ModTriggers.CLASS_CHOSEN, new ClassChosenTrigger.Instance("any"), "choose_class", "class_chosen");
            addListenerHack(player, ModTriggers.USE_ITEM, new UseItemTrigger.Instance(ItemPredicate.ANY), "progression_stone", "use_item");
            addListenerHack(player, ModTriggers.SKILL_POINT_ADDED, new SkillPointAddedTrigger.Instance("any", 1), "get_action_skill", "skill_point_added");
            addListenerHack(player, ModTriggers.USE_ACTION_SKILL, new UseActionSkillTrigger.Instance("any"), "use_action_skill", "use_action_skill");

            if (today.compareTo(lastTimePlayed) >= 1000 * 60 * 60 * 24) // Display once per day.
                Greetings.greetPlayer(player);

            StatManager.setPlayerStats(player);
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

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            PlayerData data = PlayerDataHandler.get(player);

            if (!event.isEndConquered()) {
                // The player died and respawned.
                StatManager.setPlayerStats(player);
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof IMob))
            return;

        if (Borderblocks.random.nextFloat() < 0.1f) { // TODO: Config
            ItemStack stack = CraftingItems.RELIC_FRAGMENT.getStack();
            EntityItem entityItem = new EntityItem(entity.world, entity.posX, entity.posY + entity.height / 2f, entity.posZ, stack);
            event.getDrops().add(entityItem);
        }
    }

    /**
     * A hacky(?) solution for adding advancement listeners
     *
     * @param player        The EntityPlayerMP (needed)
     * @param trigger       ModTriggers.(whatever) (probably needed)
     * @param instance      CustomTrigger.Instance instance (probably needed? constructor argument
     *                      does not seem to matter)
     * @param name          The advancement name (only need to add listener for one advancement of
     *                      this type? um, what?)
     * @param criterionName The criterion name (idk if this is needed)
     */
    private <T extends AbstractCriterionInstance> void addListenerHack(EntityPlayerMP player, ICriterionTrigger<T> trigger, T instance, String name, String criterionName) {
        ResourceLocation resource = new ResourceLocation(Borderblocks.RESOURCE_PREFIX + name);
        Advancement advancement = player.server.getAdvancementManager().getAdvancement(resource);
        trigger.addListener(player.getAdvancements(), new ICriterionTrigger.Listener<T>(instance, advancement, criterionName));
    }
}
