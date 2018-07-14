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

package net.silentchaos512.borderblocks.lib.skill.action;

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
        return 120f;
    }

    @Override
    public float getSkillDuration(PlayerData data) {
        return 60f;
    }
}
