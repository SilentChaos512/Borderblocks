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

package net.silentchaos512.borderblocks.config;

import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.lib.config.ConfigBase;
import net.silentchaos512.lib.config.ConfigMultiValueLineParser;
import net.silentchaos512.lib.config.ConfigOptionOreGen;

public class Config extends ConfigBase {

    /*
     * Categories
     */

    private static final String CAT_DEBUG = "debug";
    private static final String CAT_NETWORK = "network";
    private static final String CAT_PLAYER = "player";
    private static final String CAT_TEST = "test";
    private static final String CAT_WORLD = "world";

    /*
     * Debug
     */

    public static boolean DEBUG_MODE;
    private static final boolean DEBUG_MODE_DEFAULT = false;
    private static final String DEBUG_MODE_COMMENT = "Enables debug mode, which may or may not draw text to the screen or output additional info to log files.";

    /*
     * Network
     */

    public static int PACKET_DELAY;
    private static final int PACKET_DELAY_DEFAULT = 20;
    private static final String PACKET_DELAY_COMMENT = "How often to sync player data with clients. Less frequent updates (higher numbers) could reduce network traffic, but may cause more noticable desync.";

    /*
     * Player
     */

    public static int PLAYER_FIRST_SKILL_POINT_LEVEL;
    private static final int PLAYER_FIRST_SKILL_POINT_LEVEL_DEFAULT = 5;
    private static final String PLAYER_FIRST_SKILL_POINT_LEVEL_COMMENT = "The level players gain their first skill point.";

    public static int PLAYER_MAX_LEVEL;
    private static final int PLAYER_MAX_LEVEL_DEFAULT = 50;
    private static final String PLAYER_MAX_LEVEL_COMMENT = "The maximum level a player can achieve.";

    public static float PLAYER_HEALTH_PER_LEVEL;
    private static final float PLAYER_HEALTH_PER_LEVEL_DEFAULT = 0.8f;
    private static final String PLAYER_HEALTH_PER_LEVEL_COMMENT = "The amount of extra health (half hearts) a player gains per level.";

    public static float PLAYER_DAMAGE_PER_LEVEL;
    private static final float PLAYER_DAMAGE_PER_LEVEL_DEFAULT = 0.3f;
    private static final String PLAYER_DAMAGE_PER_LEVEL_COMMENT = "The amount of extra attack damage a player gains per level.";

    /*
     * Test
     */

    private static int TEST_VALUE;
    private static final int TEST_VALUE_DEFAULT = 42;
    private static final String TEST_VALUE_COMMENT = "A value that does nothing and everything. Do not stare too closely.";
    private static int TEST_COLOR;
    private static final int TEST_COLOR_DEFAULT = 0x9ACDEF;
    private static final String TEST_COLOR_COMMENT = "Testing ConfigBase#loadColorCode";

    /*
     * World
     */

    public static ConfigOptionOreGen ERIDIUM_ORE_GEN;

    public static final Config instance = new Config();

    public Config() {
        super(Borderblocks.MOD_ID);
    }

    @Override
    public void load() {
        try {
            ConfigMultiValueLineParser parser;

            /*
             * Debug category
             */

            config.setCategoryComment(CAT_DEBUG, "Settings used for debugging purposes. Not meant for use in standard play.");
            DEBUG_MODE = loadBoolean("Debug Mode", CAT_DEBUG, DEBUG_MODE_DEFAULT, DEBUG_MODE_COMMENT);

            /*
             * Network category
             */

            PACKET_DELAY = loadInt("Packet Delay", CAT_NETWORK, PACKET_DELAY_DEFAULT, 1, 1000, PACKET_DELAY_COMMENT);

            /*
             * Player category
             */

            PLAYER_MAX_LEVEL = loadInt("Max Level", CAT_PLAYER, PLAYER_MAX_LEVEL_DEFAULT, 1, 100, PLAYER_MAX_LEVEL_COMMENT);
            PLAYER_FIRST_SKILL_POINT_LEVEL = loadInt("First Skill Point Level", CAT_PLAYER, PLAYER_FIRST_SKILL_POINT_LEVEL_DEFAULT, 1, 100, PLAYER_FIRST_SKILL_POINT_LEVEL_COMMENT);
            PLAYER_MAX_LEVEL = PLAYER_MAX_LEVEL_DEFAULT;
            PLAYER_FIRST_SKILL_POINT_LEVEL = PLAYER_FIRST_SKILL_POINT_LEVEL_DEFAULT;
            PLAYER_HEALTH_PER_LEVEL = loadFloat("Health Per Level", CAT_PLAYER, PLAYER_HEALTH_PER_LEVEL_DEFAULT, PLAYER_HEALTH_PER_LEVEL_COMMENT);
            PLAYER_DAMAGE_PER_LEVEL = loadFloat("Damage Per Level", CAT_PLAYER, PLAYER_DAMAGE_PER_LEVEL_DEFAULT, PLAYER_DAMAGE_PER_LEVEL_COMMENT);

            /*
             * Test category
             */

            config.setCategoryComment(CAT_TEST, "The category that should not exist.");
            TEST_VALUE = loadInt("Test Value", CAT_TEST, TEST_VALUE_DEFAULT, -2, 69, TEST_VALUE_COMMENT);
            TEST_COLOR = loadColorCode("Test Color", CAT_TEST, TEST_COLOR_DEFAULT, false, TEST_COLOR_COMMENT);
            Borderblocks.log.debug("Test Color = " + TEST_COLOR);

            /*
             * World category
             */

            ERIDIUM_ORE_GEN = new ConfigOptionOreGen("Eridium Ore", 0, 0.5f, 12, 6, 12);
            ERIDIUM_ORE_GEN.loadValue(config, CAT_WORLD).validate();
        } catch (Exception ex) {
            Borderblocks.log.error("Could not load configuration file!");
            Borderblocks.log.catching(ex);
        }
    }

    @Override
    public int loadColorCode(String key, String category, int defaultValue, boolean includeAlpha, String comment) {
        // Just add exception handling to ConfigBase's version.
        try {
            return super.loadColorCode(key, category, defaultValue, includeAlpha, comment);
        } catch (NumberFormatException ex) {
            Borderblocks.log.warn("Could not load color code for config \"{}\"! Check your config to make sure you entered a valid HTML color code.", key);
            return 0;
        }
    }
}
