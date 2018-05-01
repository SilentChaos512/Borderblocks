package net.silentchaos512.borderblocks.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.client.ClientTickHandler;
import net.silentchaos512.lib.network.MessageSL;

public class MessagePlaySound extends MessageSL {

  public String soundId;
  public float volume;
  public float pitch;

  public MessagePlaySound() {

  }

  public MessagePlaySound(SoundEvent sound, float volume, float pitch) {

    this.soundId = sound.getRegistryName().toString();
    this.volume = volume;
    this.pitch = pitch;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IMessage handleMessage(MessageContext ctx) {

    ClientTickHandler.scheduledActions.add(() -> {
      EntityPlayer player = Borderblocks.proxy.getClientPlayer();
      if (player != null) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundId));
        if (sound != null) {
          player.playSound(sound, volume, pitch);
        }
      }
    });

    return null;
  }

}
