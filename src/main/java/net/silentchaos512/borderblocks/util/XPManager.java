package net.silentchaos512.borderblocks.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public class XPManager {

  public static enum XPSource {

    GENERIC, COMBAT, MINING, ADVANCEMENT;
  }

  public static XPManager INSTANCE = new XPManager();

  final Map<String, Integer> advancementXp = new HashMap<>();
  final Map<String, Integer> oreBonusXp = new HashMap<>();

  private XPManager() {

    addOreBonusXpAmounts();
    addAdvancementXpAmounts();
  }

  public static final int MOB_KILL_XP_BASE = 80;
  public static final float MOB_KILL_XP_PER_HEALTH = 2.0f;

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onLivingKilled(LivingDeathEvent event) {

    if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer)
      awardXpForKill(event.getEntityLiving(), (EntityPlayer) event.getSource().getTrueSource(), true);
  }

  public void awardXpForKill(EntityLivingBase killed, EntityPlayer player, boolean teamShare) {

    if (killed.world.isRemote)
      return;

    // TODO: Need a better method for kill XP. Currently just basing off of max health.
    float maxHealth = killed.getMaxHealth();
    int amount = Math.round(maxHealth * MOB_KILL_XP_PER_HEALTH);

    if (maxHealth > 5)
      amount += MOB_KILL_XP_BASE;
    if (killed instanceof EntitySlime)
      amount /= 2;

    awardXp(player, amount, teamShare, XPSource.COMBAT);
  }

  public static final float MIN_BLOCK_HARDNESS = 0.5f;
  public static final float MAX_BLOCK_HARDNESS = 20f;
  public static final float BLOCK_XP_PER_HARDNESS = 2.0f;
  public static final float ORE_BONUS_PER_LEVEL = 0.05f;

  private int getOreBonusXp(String oreName, PlayerData data) {

    int base = 25;
    if (oreBonusXp.containsKey(oreName)) {
      base = oreBonusXp.get(oreName);
    }
    return Math.round(base * (1f + ORE_BONUS_PER_LEVEL * data.getLevel()));
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onBlockBreak(BreakEvent event) {

    if (event.getPlayer() == null || event.getPlayer().capabilities.isCreativeMode)
      return;

    IBlockState state = event.getState();
    float hardness = state.getBlockHardness(event.getWorld(), event.getPos());
    if (hardness < MIN_BLOCK_HARDNESS) {
      return;
    }
    hardness = MathHelper.clamp(hardness, MIN_BLOCK_HARDNESS, MAX_BLOCK_HARDNESS);

    int oreBonus = 0;
    Block block = state.getBlock();
    int blockMeta = state.getBlock().getMetaFromState(state);
    if (block == Blocks.LIT_REDSTONE_ORE) {
      block = Blocks.REDSTONE_ORE;
      blockMeta = 0;
    }

    PlayerData data = PlayerDataHandler.get(event.getPlayer());

    ItemStack blockStack = new ItemStack(block, 1, blockMeta);
    for (int oreId : OreDictionary.getOreIDs(blockStack)) {
      String oreName = OreDictionary.getOreName(oreId);
      if (oreName.startsWith("ore")) {
        oreBonus = getOreBonusXp(oreName, data);
        break;
      }
    }

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

    int amount = advancementXp.containsKey(id) ? advancementXp.get(id) : -1;
    if (amount < 0 && !id.startsWith("minecraft:recipes/")) {
      // There is no value in the map. Base XP off the frame.
      net.minecraft.advancements.DisplayInfo displayInfo = event.getAdvancement().getDisplay();
      if (displayInfo != null && displayInfo.shouldAnnounceToChat()) {
        switch (displayInfo.getFrame()) {
          case CHALLENGE:
            amount = 5000;
            break;
          case GOAL:
            amount = 1000;
            break;
          case TASK:
            amount = 250;
            break;
          default:
            break;
        }
      }
    }

    if (amount > 0) {
      String string = "%s earned %d XP for advancement %s";
      Borderblocks.log.info(String.format(string, player.getName(), amount, id));
      awardXp(player, amount, false, XPSource.ADVANCEMENT);
    }
  }

  public static void awardXp(EntityPlayer player, int amount, boolean shareWithTeam) {

    awardXp(player, amount, shareWithTeam, XPSource.GENERIC);
  }

  public static void awardXp(EntityPlayer player, int amount, boolean shareWithTeam, XPSource source) {

    PlayerData data = PlayerDataHandler.get(player);
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
      return Arrays.asList(PlayerDataHandler.get(player));

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