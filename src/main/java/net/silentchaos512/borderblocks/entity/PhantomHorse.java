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

package net.silentchaos512.borderblocks.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.PlayerDataHandler.PlayerData;

public class PhantomHorse extends EntityHorse {

    private static final double BASE_MAX_HEALTH = 40.0;
    private static final double BASE_MOVEMENT_SPEED = 0.175;
    private static final double BASE_JUMP_STRENGTH = 0.5;

    private int timer = 1200;

    public PhantomHorse(World world) {
        super(world);
    }

    public PhantomHorse(World world, EntityPlayer owner, int duration) {
        super(world);
        setOwnerUniqueId(owner.getUniqueID());
        this.timer = duration;

        PlayerData data = PlayerDataHandler.get(owner);
        int healthBoostLevel = data.getPointsInSkill(SkillList.PHANTOM_MOUNT_HEALTH_UP);
        int jumpBoostLevel = data.getPointsInSkill(SkillList.PHANTOM_MOUNT_JUMP_UP);
        int speedBoostLevel = data.getPointsInSkill(SkillList.PHANTOM_MOUNT_SPEED_UP);
        AttributeModifier modHealth = new AttributeModifier("phantom_health_boost", healthBoostLevel * SkillList.PHANTOM_MOUNT_HEALTH_UP.getModifierAmount(), 1);
        AttributeModifier modJump = new AttributeModifier("phantom_jump_boost", jumpBoostLevel * SkillList.PHANTOM_MOUNT_JUMP_UP.getModifierAmount(), 1);
        AttributeModifier modSpeed = new AttributeModifier("phantom_speed_boost", speedBoostLevel * SkillList.PHANTOM_MOUNT_SPEED_UP.getModifierAmount(), 1);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(modHealth);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(modSpeed);
        this.getEntityAttribute(JUMP_STRENGTH).applyModifier(modJump);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        // TODO
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BASE_MAX_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BASE_MOVEMENT_SPEED);
        this.getEntityAttribute(JUMP_STRENGTH).setBaseValue(BASE_JUMP_STRENGTH);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = !itemstack.isEmpty();

        if (this.isBeingRidden()) {
            return false;
        }

        if (flag && itemstack.interactWithEntity(player, this, hand)) {
            return true;
        }

        this.mountTo(player);
        return true;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return false;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier / 4f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote && --this.timer <= 0) {
            this.setDead();
        }
    }

    @Override
    protected float getSoundPitch() {
        return 0.25f * super.getSoundPitch();
    }
}
