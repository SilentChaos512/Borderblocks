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

package net.silentchaos512.borderblocks.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.lib.registry.SRegistry;

public class ModSounds {
    public static final SoundEvent LEVEL_UP = create("level_up");

    public static void registerAll(SRegistry reg) {
        reg.registerSoundEvent(LEVEL_UP, "level_up");
    }

    private static SoundEvent create(String soundId) {
        ResourceLocation name = new ResourceLocation(Borderblocks.MOD_ID, soundId);
        return new SoundEvent(name);
    }
}
