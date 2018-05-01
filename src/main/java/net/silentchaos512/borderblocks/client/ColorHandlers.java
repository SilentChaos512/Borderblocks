package net.silentchaos512.borderblocks.client;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.silentchaos512.borderblocks.init.ModItems;

public class ColorHandlers {

  public static void init() {

    ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

    // Progression Relics
    itemColors.registerItemColorHandler(new IItemColor() {

      @Override
      public int colorMultiplier(ItemStack stack, int tintIndex) {

        if (tintIndex == 1) {
          float period = 40f;
          int color = Color.HSBtoRGB((ClientTickHandler.ticksInGame % period) / period, 0.6f, 1.0f);
          return color;
        }
        return 0xFFFFFF;
      }
    }, ModItems.progressionRelic);
  }
}
