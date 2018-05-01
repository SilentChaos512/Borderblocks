package net.silentchaos512.borderblocks.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.config.Config;

public class GuiFactoryBB implements IModGuiFactory {

  @Override
  public void initialize(Minecraft minecraftInstance) {

  }

  @Override
  public boolean hasConfigGui() {

    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {

    return new GuiConfigBB(parentScreen);
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

    return null;
  }

  public static class GuiConfigBB extends GuiConfig {

    public GuiConfigBB(GuiScreen parentScreen) {

      super(parentScreen, getAllElements(), Borderblocks.MOD_ID, false, false, String.format("%s Config", Borderblocks.MOD_NAME));
    }

    public static List<IConfigElement> getAllElements() {

      List<IConfigElement> list = new ArrayList<>();

      Set<String> categories = Config.instance.getConfiguration().getCategoryNames();
      for (String s : categories)
        if (!s.contains("."))
          list.add(new DummyConfigElement.DummyCategoryElement(s, s, new ConfigElement(Config.instance.getCategory(s)).getChildElements()));

      return list;
    }
  }
}
