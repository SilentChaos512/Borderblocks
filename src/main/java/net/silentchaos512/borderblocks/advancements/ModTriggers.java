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

package net.silentchaos512.borderblocks.advancements;

import net.silentchaos512.lib.registry.SRegistry;

public class ModTriggers {

    public static ClassChosenTrigger CLASS_CHOSEN;
    public static SkillPointAddedTrigger SKILL_POINT_ADDED;
    public static UseActionSkillTrigger USE_ACTION_SKILL;
    public static UseItemTrigger USE_ITEM;

    public static void init(SRegistry registry) {
        CLASS_CHOSEN = (ClassChosenTrigger) registry.registerAdvancementTrigger(new ClassChosenTrigger());
        SKILL_POINT_ADDED = (SkillPointAddedTrigger) registry.registerAdvancementTrigger(new SkillPointAddedTrigger());
        USE_ACTION_SKILL = (UseActionSkillTrigger) registry.registerAdvancementTrigger(new UseActionSkillTrigger());
        USE_ITEM = (UseItemTrigger) registry.registerAdvancementTrigger(new UseItemTrigger());
    }
}
