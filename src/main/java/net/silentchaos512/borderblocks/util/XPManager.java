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

import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import java.util.*;

public class XPManager {

    public enum XPSource {
        GENERIC, COMBAT, MINING, ADVANCEMENT;
    }

    public static XPManager INSTANCE = new XPManager();

    private final Map<String, Integer> advancementXp = new HashMap<>();
    private final Map<String, Integer> oreBonusXp = new HashMap<>();

    private XPManager() {
        addOreBonusXpAmounts();
        addAdvancementXpAmounts();
    }

    private static final int MOB_KILL_XP_BASE = 80;
    private static final float MOB_KILL_XP_PER_HEALTH = 2.0f;
    private static final float MOB_KILL_XP_PER_ARMOR_SQ = 0.5f;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingKilled(LivingDeathEvent event) {
        if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer)
            awardXpForKill(event.getEntityLiving(), (EntityPlayer) event.getSource().getTrueSource(), true);
    }

    public void awardXpForKill(EntityLivingBase killed, EntityPlayer player, boolean teamShare) {
        if (killed.world.isRemote)
            return;

        float maxHealth = killed.getMaxHealth();
        float armor = killed.getTotalArmorValue();
        float healthFactor = maxHealth * MOB_KILL_XP_PER_HEALTH;
        float armorFactor = armor * armor * MOB_KILL_XP_PER_ARMOR_SQ;
        int amount = Math.round(healthFactor + armorFactor);

        if (maxHealth > 5)
            amount += MOB_KILL_XP_BASE;
        if (killed instanceof EntitySlime)
            amount /= 2;

        awardXp(player, amount, teamShare, XPSource.COMBAT);
    }

    private static final float MIN_BLOCK_HARDNESS = 0.5f;
    private static final float MAX_BLOCK_HARDNESS = 20f;
    private static final float BLOCK_XP_PER_HARDNESS = 2.0f;
    private static final float ORE_BONUS_PER_LEVEL = 0.05f;

    private int getOreBonusXp(IBlockState state, PlayerData data) {
        if (data == null) return 0;
        int base = 0;

        // Get block, item dropped, and meta values
        Block block = state.getBlock();
        int blockMeta = block.getMetaFromState(state);
        if (block == Blocks.LIT_REDSTONE_ORE) {
            block = Blocks.REDSTONE_ORE;
            blockMeta = 0;
        }
        Item droppedItem = block.getItemDropped(state, Borderblocks.random, 0);
        int droppedMeta = block.damageDropped(state);

        // Get stack of ore block and dropped item
        ItemStack blockStack = new ItemStack(block, 1, blockMeta);
        ItemStack dropStack = new ItemStack(droppedItem, 1, droppedMeta);

        // Search through ore dictionary keys
        List<String> oreNames = StackHelper.getOreNames(blockStack);
        oreNames.addAll(StackHelper.getOreNames(dropStack));
        boolean isOre = false;
        for (String oreName : oreNames) {
            if (oreBonusXp.containsKey(oreName)) {
                base = oreBonusXp.get(oreName);
                isOre = true;
            } else if (oreName.startsWith("ore")) {
                isOre = true;
            }
        }

        // Seems to be an ore, but doesn't have a specific value listed;
        if (isOre && base <= 0)
            base = 25;

        // Bonus based on player level
        return Math.round(base * (1f + ORE_BONUS_PER_LEVEL * data.getLevel()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BreakEvent event) {
        if (event.getPlayer() == null || event.getPlayer() instanceof FakePlayer || event.getPlayer().capabilities.isCreativeMode)
            return;

        IBlockState state = event.getState();

        // Get block hardness, clamp to min-max
        float hardness = state.getBlockHardness(event.getWorld(), event.getPos());
        if (hardness < MIN_BLOCK_HARDNESS)
            return;
        hardness = MathHelper.clamp(hardness, MIN_BLOCK_HARDNESS, MAX_BLOCK_HARDNESS);

        // Ore bonus
        PlayerData data = PlayerDataHandler.get(event.getPlayer());
        int oreBonus = getOreBonusXp(state, data);

        // Wood is too hard (insert jokes here)
        if (state.getMaterial() == Material.WOOD)
            hardness /= 2f;

        int clamp = Math.round(BLOCK_XP_PER_HARDNESS * hardness);
        clamp = clamp < 1 ? 1 : clamp;
        awardXp(event.getPlayer(), oreBonus > 0 ? oreBonus : clamp, true, XPSource.MINING);
    }

    @SubscribeEvent
    public void onAdvancement(AdvancementEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        String id = event.getAdvancement().getId().toString();
        Borderblocks.log.debug("handle advancement " + id);

        int amount = advancementXp.getOrDefault(id, -1);
        if (amount < 0 && !id.startsWith("minecraft:recipes/")) {
            // There is no value in the map. Base XP off the frame.
            amount = getGenericAdvancementXp(event.getAdvancement(), player);
        }

        if (amount > 0) {
            Borderblocks.log.info("{} earned {} XP for advancement \"{}\"", player.getName(), amount, id);
            awardXp(player, amount, false, XPSource.ADVANCEMENT);
        }
    }

    private static final int XP_BASE_TASK = 250;
    private static final int XP_BASE_GOAL = 1000;
    private static final int XP_BASE_CHALLENGE = 5000;
    private static final float XP_MULTI_PER_PARENT = 0.1f;

    public int getGenericAdvancementXp(@Nonnull Advancement advancement, @Nonnull EntityPlayer player) {
        int amount = -1;
        net.minecraft.advancements.DisplayInfo displayInfo = advancement.getDisplay();

        if (displayInfo != null && displayInfo.shouldAnnounceToChat()) {
            switch (displayInfo.getFrame()) {
                case CHALLENGE:
                    amount = XP_BASE_CHALLENGE;
                    break;
                case GOAL:
                    amount = XP_BASE_GOAL;
                    break;
                case TASK:
                    amount = XP_BASE_TASK;
                    break;
                default:
                    Borderblocks.log.warn("Unknown advancement frame type: {}", displayInfo.getFrame().name());
                    return -1;
            }
        }

        // Increase XP further down the tree. Limit to max of 20... just in case.
        int parentCount = 0;
        Advancement adv = advancement;
        while (adv.getParent() != null && parentCount < 20) {
            adv = adv.getParent();
            ++parentCount;
        }

        amount *= 1 + XP_MULTI_PER_PARENT * parentCount;

        return amount;
    }

    public static void awardXp(EntityPlayer player, int amount, boolean shareWithTeam) {
        awardXp(player, amount, shareWithTeam, XPSource.GENERIC);
    }

    public static void awardXp(EntityPlayer player, int amount, boolean shareWithTeam, XPSource source) {
        PlayerData data = PlayerDataHandler.get(player);
        if (data == null) return;
        if (amount > 0 && !data.client) {
            switch (source) {
                case ADVANCEMENT:
                    for (PlayerData playerData : getTeamMembers(player, shareWithTeam))
                        playerData.addAdvancementXp(amount);
                    break;
                case MINING:
                    for (PlayerData playerData : getTeamMembers(player, shareWithTeam))
                        playerData.addMiningXp(amount);
                    break;
                case COMBAT:
                    for (PlayerData playerData : getTeamMembers(player, shareWithTeam))
                        playerData.addCombatXp(amount);
                    break;
                case GENERIC:
                default:
                    for (PlayerData playerData : getTeamMembers(player, shareWithTeam))
                        playerData.addXp(amount);
                    break;
            }
        }
    }

    private static List<PlayerData> getTeamMembers(EntityPlayer player, boolean teamShare) {
        Team team = player.getTeam();
        if (team == null || !teamShare)
            return Collections.singletonList(PlayerDataHandler.get(player));

        List<PlayerData> list = new ArrayList<>();
        for (String name : team.getMembershipCollection()) {
            EntityPlayer teamPlayer = player.world.getPlayerEntityByName(name);
            if (teamPlayer != null && player.getDistanceSq(teamPlayer) < 16384) {
                PlayerData playerData = PlayerDataHandler.get(teamPlayer);
                if (playerData != null)
                    list.add(playerData);
            }
        }
        return list;
    }

    private void addOreBonusXpAmounts() {
        oreBonusXp.put("oreCoal", 20);
        oreBonusXp.put("oreIron", 30);
        oreBonusXp.put("oreGold", 50);
        oreBonusXp.put("oreDiamond", 70);
        oreBonusXp.put("oreEmerald", 80);
        oreBonusXp.put("oreLapis", 30);
        oreBonusXp.put("oreRedstone", 30);
        oreBonusXp.put("oreEridium", 100);
        oreBonusXp.put("oreCopper", 20);
        oreBonusXp.put("oreTin", 20);
        oreBonusXp.put("oreSilver", 40);
        oreBonusXp.put("oreLead", 30);
        oreBonusXp.put("oreAluminum", 30);
        oreBonusXp.put("oreAluminium", 30);
        oreBonusXp.put("oreNickel", 40);
        oreBonusXp.put("oreOsmium", 30);
        oreBonusXp.put("orePlatinum", 60);
        oreBonusXp.put("oreTitanium", 60);
    }

    private void addAdvancementXpAmounts() {
        advancementXp.put("minecraft:story/root", 0);
        advancementXp.put("minecraft:story/mine_stone", 150);
        advancementXp.put("minecraft:story/upgrade_tools", 200);
        advancementXp.put("minecraft:story/smelt_iron", 300);
        advancementXp.put("minecraft:story/obtain_armor", 300);
        advancementXp.put("minecraft:story/lava_bucket", 300);
        advancementXp.put("minecraft:story/iron_tools", 500);
        advancementXp.put("minecraft:story/deflect_arrow", 500);
        advancementXp.put("minecraft:story/form_obsidian", 500);
        advancementXp.put("minecraft:story/mine_diamond", 1000);
        advancementXp.put("minecraft:story/enter_the_nether", 1000);
        advancementXp.put("minecraft:story/shiny_gear", 1000);
        advancementXp.put("minecraft:story/enchant_item", 1000);
        advancementXp.put("minecraft:story/cure_zombie_villager", 2500);
        advancementXp.put("minecraft:story/follow_ender_eye", 1000);
        advancementXp.put("minecraft:story/enter_the_end", 2500);

        advancementXp.put("minecraft:nether/root", 0);
        advancementXp.put("minecraft:nether/fast_travel", 6000);
        advancementXp.put("minecraft:nether/find_fortress", 3000);
        advancementXp.put("minecraft:nether/return_to_sender", 6000);
        advancementXp.put("minecraft:nether/obtain_blaze_rod", 3000);
        advancementXp.put("minecraft:nether/get_wither_skull", 3000);
        advancementXp.put("minecraft:nether/uneasy_alliance", 6000);
        advancementXp.put("minecraft:nether/brew_potion", 1200);
        advancementXp.put("minecraft:nether/summon_wither", 6000);
        advancementXp.put("minecraft:nether/all_potions", 6000);
        advancementXp.put("minecraft:nether/create_beacon", 6000);
        advancementXp.put("minecraft:nether/all_effects", 12000);
        advancementXp.put("minecraft:nether/create_full_beacon", 12000);

        advancementXp.put("minecraft:end/root", 0);
        advancementXp.put("minecraft:end/kill_dragon", 10000);
        advancementXp.put("minecraft:end/dragon_egg", 5000);
        advancementXp.put("minecraft:end/enter_end_gateway", 5000);
        advancementXp.put("minecraft:end/respawn_dragon", 8000);
        advancementXp.put("minecraft:end/dragon_breath", 2500);
        advancementXp.put("minecraft:end/find_end_city", 8000);
        advancementXp.put("minecraft:end/elytra", 5000);
        advancementXp.put("minecraft:end/levitate", 10000);

        advancementXp.put("minecraft:adventure/root", 0);
        advancementXp.put("minecraft:adventure/kill_a_mob", 200);
        advancementXp.put("minecraft:adventure/trade", 700);
        advancementXp.put("minecraft:adventure/sleep_in_bed", 500);
        advancementXp.put("minecraft:adventure/throw_trident", 250); // 1.13?
        advancementXp.put("minecraft:adventure/shoot_arrow", 300);
        advancementXp.put("minecraft:adventure/kill_all_mobs", 10000);
        advancementXp.put("minecraft:adventure/totem_of_undying", 7000);
        advancementXp.put("minecraft:adventure/summon_iron_golem", 5000);
        advancementXp.put("minecraft:adventure/adventuring_time", 10000);
        advancementXp.put("minecraft:adventure/very_very_frightening", 250); // 1.13?
        advancementXp.put("minecraft:adventure/sniper_duel", 10000);

        advancementXp.put("minecraft:husbandry/root", 0);
        advancementXp.put("minecraft:husbandry/breed_an_animal", 300);
        advancementXp.put("minecraft:husbandry/tame_an_animal", 300);
        advancementXp.put("minecraft:husbandry/plant_seed", 300);
        advancementXp.put("minecraft:husbandry/fishy_business", 250); // 1.13?
        advancementXp.put("minecraft:husbandry/bred_all_animals", 7000);
        advancementXp.put("minecraft:husbandry/balanced_diet", 7000);
        advancementXp.put("minecraft:husbandry/break_diamond_hoe", 10000);
        advancementXp.put("minecraft:husbandry/tactical_fishing", 250); // 1.13?

        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "root", 0);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "get_scrap", 100);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "choose_class", 150);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "get_action_skill", 200);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "use_action_skill", 300);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "progression_stone", 500);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "progression_iron", 2000);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "progression_diamond", 5000);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "progression_eridium", 15000);
        advancementXp.put(Borderblocks.RESOURCE_PREFIX + "mine_eridium", 3000);
    }
}
