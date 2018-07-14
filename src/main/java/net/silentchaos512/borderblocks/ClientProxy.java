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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.borderblocks.client.ClientTickHandler;
import net.silentchaos512.borderblocks.client.ColorHandlers;
import net.silentchaos512.borderblocks.client.DebugDisplayHandler;
import net.silentchaos512.borderblocks.client.TextEffectDisplayHandler;
import net.silentchaos512.borderblocks.client.HudDisplayHandler;
import net.silentchaos512.borderblocks.client.key.KeyTracker;
import net.silentchaos512.lib.registry.SRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        super.preInit(registry, event);

        // Client-only event handlers
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(KeyTracker.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new HudDisplayHandler());
        MinecraftForge.EVENT_BUS.register(TextEffectDisplayHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new DebugDisplayHandler());

        registry.clientPreInit(event);

        // Mod entity renderers?
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        super.init(registry, event);
        registry.clientInit(event);

        ColorHandlers.init();
    }

    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        super.postInit(registry, event);
        registry.clientPostInit(event);
    }

    @Override
    public EntityPlayer getClientPlayer() {

        return Minecraft.getMinecraft().player;
    }

    @Override
    public int getParticleSettings() {

        return Minecraft.getMinecraft().gameSettings.particleSetting;
    }
}
