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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.advancements.ModTriggers;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.lib.ProgressionTier;
import net.silentchaos512.borderblocks.lib.character.CharacterClass;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.lib.skill.action.ActionSkill;
import net.silentchaos512.borderblocks.network.MessageDataSync;
import net.silentchaos512.borderblocks.network.MessageXpGain;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.TimeHelper;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;

public class PlayerDataHandler {
    private static final String NBT_ROOT = Borderblocks.MOD_ID + "_data";
    private static Map<Integer, PlayerData> playerData = new HashMap();

    @Nullable
    public static PlayerData get(EntityPlayer player) {
        if (player == null || player instanceof FakePlayer) {
            return null;
        }

        int key = getKey(player);
        if (!playerData.containsKey(key)) {
            playerData.put(key, new PlayerData(player));
        }

        PlayerData data = playerData.get(key);
        if (data != null && data.playerWR.get() != player) {
            NBTTagCompound tags = new NBTTagCompound();
            data.writeToNBT(tags);
            playerData.remove(key);
            data = get(player);
            data.readFromNBT(tags);
        }

        return data;
    }

    public static void cleanup() {
        List<Integer> remove = new ArrayList<>();

        for (int i : playerData.keySet()) {
            PlayerData d = playerData.get(i);
            if (d != null && d.playerWR.get() == null) {
                remove.add(i);
            }
        }

        for (int i : remove) {
            playerData.remove(i);
        }
    }

    private static int getKey(EntityPlayer player) {
        return player.hashCode() << 1 + (player.world.isRemote ? 1 : 0);
    }

    public static NBTTagCompound getDataCompoundForPlayer(EntityPlayer player) {
        NBTTagCompound forgeData = player.getEntityData();
        if (!forgeData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            forgeData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }

        NBTTagCompound persistentData = forgeData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persistentData.hasKey(NBT_ROOT)) {
            persistentData.setTag(NBT_ROOT, new NBTTagCompound());
        }

        return persistentData.getCompoundTag(NBT_ROOT);
    }

    public static class EventHandler {

        @SubscribeEvent
        public void onServerTick(ServerTickEvent event) {
            if (event.phase == Phase.END) {
                PlayerDataHandler.cleanup();
            }
        }

        @SubscribeEvent
        public void onPlayerTick(LivingUpdateEvent event) {
            if (event.getEntityLiving() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getEntityLiving();
                PlayerDataHandler.get(player).tick();
            }
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerLoggedInEvent event) {
            if (event.player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) event.player;

                MessageDataSync message = new MessageDataSync(get(event.player), event.player);
                Borderblocks.network.wrapper.sendTo(message, playerMP);
            }
        }
    }

    public static class PlayerData {
        static final String NBT_ACTIVE_KILL_SKILLS = "active_kill_skills";
        static final String NBT_CHARACTER_CLASS = "character_class";
        static final String NBT_COOLDOWN = "cooldown";
        static final String NBT_LAST_LOGIN = "last_login";
        static final String NBT_LEVEL = "level";
        static final String NBT_LEVEL_SET_BY_COMMAND = "level_set_by_command";
        static final String NBT_PROGRESSION_TIER = "progression_tier";
        static final String NBT_SKILL_DURATION = "skill_duration";
        static final String NBT_SKILLS = "skills";
        static final String NBT_XP = "xp";

        // XP/character/skills/progression
        @Getter(value = AccessLevel.PUBLIC)
        int level = 1;
        @Getter(value = AccessLevel.PUBLIC)
        int xp = 0;
        @Getter(value = AccessLevel.PUBLIC)
        float cooldown = 0f;
        @Getter(value = AccessLevel.PUBLIC)
        float cooldownRate = 1f;
        @Getter(value = AccessLevel.PUBLIC)
        int actionSkillDuration = 0;
        @Getter(value = AccessLevel.PUBLIC)
        @Setter(value = AccessLevel.PUBLIC)
        ProgressionTier progressionTier = ProgressionTier.WOOD;
        @Getter(value = AccessLevel.PUBLIC)
        @Setter(value = AccessLevel.PUBLIC)
        CharacterClass characterClass = CharacterClass.CLASS_UNDEFINED;
        final Map<Skill, Integer> skills = new HashMap<>();
        @Getter(value = AccessLevel.PUBLIC)
        final Map<Skill, Float> activeKillSkills = new HashMap<>();
        /**
         * Set true if the player's level has been set by a command. Tracked purely for statistical
         * purposes.
         */
        boolean levelSetByCommand = false;

        // Mining XP (accumulates and awards when player stops mining to prevent spam)
        static final int MINING_XP_DELAY = 50;
        int miningXpAccumulator = 0;
        int miningXpTimer = 0;
        // Combat XP (same system as mining XP)
        static final int COMBAT_XP_DELAY = 5;
        int combatXpAccumulator = 0;
        int combatXpTimer = 0;
        // Advancement XP (same system again)
        static final int ADVANCEMENT_XP_DELAY = 30;
        int advancementXpAccumulator = 0;
        int advancementXpTimer = 0;

        Calendar lastTimePlayed = Calendar.getInstance();

        public WeakReference<EntityPlayer> playerWR;
        final boolean client;
        int lastPosX = 0;
        int lastPosY = 0;
        int lastPosZ = 0;

        public PlayerData(EntityPlayer player) {
            playerWR = new WeakReference<>(player);
            client = player.world.isRemote;

            load();
        }

        public Calendar getLastTimePlayed() {
            return lastTimePlayed;
        }

        private void tick() {
            float deltaTime = 1f / 20f;

            // Action skill cooldown
            if (cooldown > 0f || actionSkillDuration > 0) {
                if (actionSkillDuration > 0) {
                    --actionSkillDuration;
                } else {
                    cooldown -= cooldownRate * deltaTime;
                    if (cooldown < 0f)
                        cooldown = 0f;
                }
            }

            if (!client) {
                EntityPlayer player = playerWR.get();
                if (player == null)
                    return;

                // Kill skills
                List<Skill> toRemove = new ArrayList<>();
                for (Entry<Skill, Float> entry : activeKillSkills.entrySet()) {
                    Skill skill = entry.getKey();
                    float time = entry.getValue() - deltaTime;
                    if (time <= 0f)
                        toRemove.add(skill);
                    activeKillSkills.put(skill, time);
                }
                for (Skill skill : toRemove)
                    activeKillSkills.remove(skill);

                // Mining XP handler
                if (miningXpTimer > 0 && --miningXpTimer == 0) {
                    addXp(miningXpAccumulator);
                    miningXpAccumulator = 0;
                }
                // Combat XP handler
                if (combatXpTimer > 0 && --combatXpTimer == 0) {
                    addXp(combatXpAccumulator);
                    combatXpAccumulator = 0;
                }
                // Advancement XP handler
                if (advancementXpTimer > 0 && --advancementXpTimer == 0) {
                    addXp(advancementXpAccumulator);
                    advancementXpAccumulator = 0;
                }

                if (player.world.getTotalWorldTime() % 20 == 0) {
                    lastPosX = (int) player.posX;
                    lastPosY = (int) player.posY;
                    lastPosZ = (int) player.posZ;
                }
                // Sync with client?
                if (player.world.getTotalWorldTime() % Config.PACKET_DELAY == 0) {
                    save();
                    sendUpdateMessage();
                }
            }
        }

        private void sendUpdateMessage() {
            if (!client) {
                EntityPlayer player = playerWR.get();
                EntityPlayerMP playerMP = (EntityPlayerMP) player;

                MessageDataSync message = new MessageDataSync(get(player), player);
                Borderblocks.network.wrapper.sendTo(message, playerMP);
            }
        }

        public void save() {
            if (!client) {
                EntityPlayer player = playerWR.get();
                if (player != null) {
                    NBTTagCompound tags = getDataCompoundForPlayer(player);
                    writeToNBT(tags);
                }
            }
        }

        public void writeToNBT(NBTTagCompound tags) {
            tags.setInteger(NBT_LEVEL, level);
            tags.setInteger(NBT_XP, xp);
            tags.setFloat(NBT_COOLDOWN, cooldown);
            tags.setInteger(NBT_SKILL_DURATION, actionSkillDuration);
            tags.setByte(NBT_PROGRESSION_TIER, (byte) progressionTier.ordinal());

            // Character Class
            tags.setString(NBT_CHARACTER_CLASS, characterClass.getClassName());

            // Skills
            NBTTagList tagSkills = new NBTTagList();
            for (Entry<Skill, Integer> entry : skills.entrySet()) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setString("id", entry.getKey().getName());
                compound.setShort("level", entry.getValue().shortValue());
                tagSkills.appendTag(compound);
            }
            tags.setTag(NBT_SKILLS, tagSkills);

            // Kill skills
            NBTTagList tagList = new NBTTagList();
            for (Entry<Skill, Float> entry : activeKillSkills.entrySet()) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setString("id", entry.getKey().getName());
                compound.setFloat("time", entry.getValue());
            }
            tags.setTag(NBT_ACTIVE_KILL_SKILLS, tagList);

            // Random stuff
            tags.setBoolean(NBT_LEVEL_SET_BY_COMMAND, levelSetByCommand);

            // Last time played (TODO: are we using this?)
            int year = lastTimePlayed.get(Calendar.YEAR);
            int month = lastTimePlayed.get(Calendar.MONTH) + 1;
            int date = lastTimePlayed.get(Calendar.DATE);
            String dateString = year + "/" + month + "/" + date;
            tags.setString(NBT_LAST_LOGIN, dateString);
        }

        public void load() {
            if (!client) {
                EntityPlayer player = playerWR.get();
                if (player != null) {
                    NBTTagCompound tags = getDataCompoundForPlayer(player);
                    readFromNBT(tags);
                }
            }
        }

        public void readFromNBT(NBTTagCompound tags) {
            level = MathHelper.clamp(tags.getInteger(NBT_LEVEL), 1, Config.PLAYER_MAX_LEVEL);
            xp = tags.getInteger(NBT_XP);
            cooldown = tags.getFloat(NBT_COOLDOWN);
            actionSkillDuration = tags.getInteger(NBT_SKILL_DURATION);
            progressionTier = ProgressionTier.byOrdinal(tags.getByte(NBT_PROGRESSION_TIER));

            // Character Class
            String className = tags.getString(NBT_CHARACTER_CLASS);
            characterClass = CharacterClass.CLASS_UNDEFINED;
            for (CharacterClass definedClass : CharacterClass.ALL_CLASSES) {
                if (definedClass.getClassName().equals(className)) {
                    characterClass = definedClass;
                    break;
                }
            }

            // Skills
            skills.clear();
            NBTTagList tagList = tags.getTagList(NBT_SKILLS, 10);
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound compound = tagList.getCompoundTagAt(i);
                String id = compound.getString("id");
                Skill skill = SkillList.getByName(id);
                if (skill != null) {
                    int level = compound.getShort("level");
                    skills.put(skill, level);
                } else {
                    String line = "Player %s has unknown skill \"%s\". Ignoring.";
                    Borderblocks.log.warning(String.format(line, playerWR.get().getName(), id));
                }
            }

            // Kill skills
            activeKillSkills.clear();
            tagList = tags.getTagList(NBT_ACTIVE_KILL_SKILLS, 10);
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound compound = tagList.getCompoundTagAt(i);
                String id = compound.getString("id");
                Skill skill = SkillList.getByName(id);
                if (skill != null) {
                    float time = compound.getFloat("time");
                    activeKillSkills.put(skill, time);
                }
            }

            // Random stuff
            levelSetByCommand = tags.getBoolean(NBT_LEVEL_SET_BY_COMMAND);

            // Last time played (TODO: are we using this?)
            String lastDatePlayed = tags.getString(NBT_LAST_LOGIN);
            String[] dateParts = lastDatePlayed.split("/");
            if (dateParts.length >= 3) {
                try {
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]) - 1;
                    int date = Integer.parseInt(dateParts[2]);
                    lastTimePlayed.set(year, month, date);
                } catch (NumberFormatException ex) {
                    Borderblocks.log.warning("Could not parse player's last login time.");
                    ex.printStackTrace();
                }
            }
        }

        /**
         * Award the player with XP, adding the given amount to their current amount.
         */
        public void addXp(int amount) {
            if (isMaxLevel())
                return;

            xp += amount;

            boolean levelUpped = false;
            while (xp >= getXpForNextLevel()) {
                doLevelUp();
                levelUpped = true;
            }

            if (playerWR.get() instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) playerWR.get();
                Borderblocks.network.wrapper.sendTo(new MessageXpGain(amount, levelUpped), player);
            }
        }

        public void addCombatXp(int amount) {
            combatXpAccumulator += amount;
            combatXpTimer = COMBAT_XP_DELAY;
        }

        /**
         * Give XP for mining. Unlike addXp, this accumulates until the player stops mining, to
         * prevent spamming the "+XP" text.
         */
        public void addMiningXp(int amount) {
            miningXpAccumulator += amount;
            miningXpTimer = MINING_XP_DELAY;
        }

        public void addAdvancementXp(int amount) {
            advancementXpAccumulator = amount;
            advancementXpTimer = ADVANCEMENT_XP_DELAY;
        }

        /**
         * Sets the player's XP to the given amount. Not recommended, use addXp instead.
         */
        public void setXp(int amount) {
            xp = amount;

            // Update level
            level = 1;
            while (xp >= getXpForNextLevel())
                doLevelUp();
        }

        /**
         * Gets the XP required to level up.
         */
        public int getXpForNextLevel() {
            return getXpForLevel(level + 1);
        }

        /**
         * Gets the XP required to achieve the given level.
         */
        public int getXpForLevel(int lvl) {
            return (int) Math.ceil(60 * Math.pow(lvl, 2.8) - 60);
        }

        public boolean isMaxLevel() {
            return level >= Config.PLAYER_MAX_LEVEL;
        }

        /**
         * <strong> Do not use! </strong> Intended for use by CommandLevel only. Directly sets the
         * players level, setting XP
         * to the appropriate values as well.
         *
         * @param isCommand TODO
         */
        public void setLevelDirectly(int lvl, boolean isCommand) {
            this.xp = getXpForLevel(lvl);
            this.level = lvl;
            this.levelSetByCommand |= isCommand;
            this.cooldown = 0f;
            StatManager.setPlayerStats(playerWR.get());
        }

        public int getAvailableSkillPoints() {
            int usedPoints = 0;
            for (int k : skills.values())
                usedPoints += k;
            return getTotalSkillPoints() - usedPoints;
        }

        /**
         * Gets the total number of skill points the player has. This is calculated based on level,
         * the value is never stored.
         */
        public int getTotalSkillPoints() {
            if (level < Config.PLAYER_FIRST_SKILL_POINT_LEVEL)
                return 0;
            return level - Config.PLAYER_FIRST_SKILL_POINT_LEVEL + 1;
        }

        /**
         * Gets the player's skills. Modifying directly is not recommended, use addPointToSkill.
         *
         * @return
         */
        public Map<Skill, Integer> getSkills() {
            return skills;
        }

        public <T extends Skill> Map<T, Integer> getSkillsOfType(Class<T> clazz) {
            Map<T, Integer> ret = new HashMap<>();
            skills.forEach((skill, level) -> {
                if (skill.getClass() == clazz)
                    ret.put((T) skill, level);
            });
            return ret;
        }

        /**
         * Add a point to (level up) a skill, or add it to the player's skills if they do not
         * already have it.
         *
         * @param skill The skill to add a point to
         * @return True if adding a point was successful, false otherwise (e.g. the skill already
         * has max points)
         */
        public boolean addPointToSkill(Skill skill) {
            if (getAvailableSkillPoints() < 1)
                return false;

            // currentPoints is 0 if skills map does not contain the skill.
            int currentPoints = getPointsInSkill(skill);

            if (currentPoints < skill.getMaxPoints()) {
                // Add a point.
                int newPoints = currentPoints + 1;
                skills.put(skill, newPoints);
                return true;
            } else {
                // Already maxed out.
                return false;
            }
        }

        public int getPointsInSkill(Skill skill) {
            return skills.containsKey(skill) ? skills.get(skill) : 0;
        }

        public void setPointsInSkill(Skill skill, int amount) {
            skills.put(skill, amount);
        }

        public boolean isActionSkillReady() {
            return cooldown <= 0f;
        }

        public void resetCooldownBonuses() {
            this.cooldownRate = 1f;
        }

        public void addCooldownBonus(float rateBoost) {
            this.cooldownRate += rateBoost;
        }

        /**
         * Level up the player, increasing level and doing anything else that may accompany a level
         * up.
         */
        private void doLevelUp() {
            ++level;
            cooldown = 0f;
            actionSkillDuration = 0;

            EntityPlayer player = playerWR.get();
            if (player != null) {
                player.heal(player.getMaxHealth() - player.getHealth());
                StatManager.setPlayerStats(player);
            }
        }

        public void respecSkills() {
            skills.clear();

            if (!client && getTotalSkillPoints() > 0) {
                String line = Borderblocks.localization.getMiscText("message.respec");
                ChatHelper.sendMessage(playerWR.get(), line);
            }
        }

        public boolean activateActionSkill(@Nullable BlockPos hitPos, EnumFacing hitSide, boolean altKeyDown) {
            ActionSkill skill = characterClass.getActionSkill();
            if (cooldown > 0f || skill == null || !skills.containsKey(skill))
                return false;

            boolean flag = skill.activate(playerWR.get(), progressionTier, hitPos, hitSide, altKeyDown);
            if (flag) {
                cooldown = skill.getCooldownTime();
                actionSkillDuration = TimeHelper.ticksFromSeconds(skill.getSkillDuration(this));
                Borderblocks.log.debug(actionSkillDuration);
                if (playerWR.get() instanceof EntityPlayerMP)
                    ModTriggers.USE_ACTION_SKILL.trigger((EntityPlayerMP) playerWR.get());
            }
            return flag;
        }

        public void activateKillSkill(Skill skill) {
            if (skill == null || !skills.containsKey(skill))
                return;
            activeKillSkills.put(skill, skill.getKillSkillDuration());
        }
    }
}
