package net.silentchaos512.borderblocks.block;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.borderblocks.init.ModItems;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

public class EridiumBlock extends Block implements IRegistryObject {

  public static final String NAME = "eridium_block";

  public EridiumBlock() {

    super(Material.IRON);
  }

  @Override
  public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {

    return true;
  }

  @Override
  public void addRecipes(RecipeMaker recipes) {

    recipes.addCompression("eridium_block", ModItems.craftingItem.eridiumIngot, new ItemStack(this), 9);
  }

  @Override
  public void addOreDict() {

    OreDictionary.registerOre("blockEridium", this);
  }

  @Override
  public String getModId() {

    return Borderblocks.MOD_ID;
  }

  @Override
  public String getName() {

    return NAME;
  }

  @Override
  public void getModels(Map<Integer, ModelResourceLocation> models) {

    models.put(0, new ModelResourceLocation(getFullName(), "normal"));
  }
}
