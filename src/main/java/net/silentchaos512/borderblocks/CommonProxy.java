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

package net.silentchaos512.borderblocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.silentchaos512.borderblocks.advancements.ModTriggers;
import net.silentchaos512.borderblocks.config.Config;
import net.silentchaos512.borderblocks.event.CommonEvents;
import net.silentchaos512.borderblocks.event.PetEvents;
import net.silentchaos512.borderblocks.event.SkillEvents;
import net.silentchaos512.borderblocks.init.ModBlocks;
import net.silentchaos512.borderblocks.init.ModEntities;
import net.silentchaos512.borderblocks.init.ModItems;
import net.silentchaos512.borderblocks.init.ModSounds;
import net.silentchaos512.borderblocks.lib.character.CharacterClass;
import net.silentchaos512.borderblocks.lib.skill.BlockDropsSkill;
import net.silentchaos512.borderblocks.lib.skill.Skill;
import net.silentchaos512.borderblocks.lib.skill.SkillList;
import net.silentchaos512.borderblocks.network.MessagePlaySound;
import net.silentchaos512.borderblocks.util.PlayerDataHandler;
import net.silentchaos512.borderblocks.util.XPManager;
import net.silentchaos512.borderblocks.world.WorldGeneratorBB;
import net.silentchaos512.lib.proxy.IProxy;
import net.silentchaos512.lib.registry.SRegistry;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        registry.preInit(event);

        Config.instance.init(event.getSuggestedConfigurationFile());

        // registry.addRegistrationHandler(new ModPotions(), Potion.class);
        registry.addRegistrationHandler(ModBlocks.INSTANCE, Block.class);
        registry.addRegistrationHandler(ModItems.INSTANCE, Item.class);
        registry.addRegistrationHandler(ModSounds.INSTANCE, SoundEvent.class);

        ModEntities.init(registry);

        CharacterClass.init();

        // Advancements
        ModTriggers.init(registry);

        // World generators
        GameRegistry.registerWorldGenerator(new WorldGeneratorBB(), 0);

        // Event handlers registered here
        MinecraftForge.EVENT_BUS.register(new PlayerDataHandler.EventHandler());
        MinecraftForge.EVENT_BUS.register(XPManager.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        MinecraftForge.EVENT_BUS.register(new PetEvents());
        MinecraftForge.EVENT_BUS.register(new SkillEvents());
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        registry.init(event);

        for (Skill skill : SkillList.ALL_SKILLS) {
            if (skill instanceof BlockDropsSkill) {
                ((BlockDropsSkill) skill).initDrops();
            }
        }

        Config.instance.save();
    }

    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        registry.postInit(event);
    }

    public void playSoundOnClient(EntityPlayer player, SoundEvent sound, float volume, float pitch) {
        if (player instanceof EntityPlayerMP) {
            Borderblocks.network.wrapper.sendTo(new MessagePlaySound(sound, volume, pitch), (EntityPlayerMP) player);
        }
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return null;
    }

    @Override
    public int getParticleSettings() {
        return 0;
    }
}
