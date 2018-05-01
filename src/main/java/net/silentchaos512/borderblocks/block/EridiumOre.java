package net.silentchaos512.borderblocks.block;

import java.util.Map;
import java.util.Random;

import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.borderblocks.Borderblocks;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

public class EridiumOre extends BlockOre implements IRegistryObject {

  private static final String NAME = "eridium_ore";

  public EridiumOre() {

    super();
    setHardness(5.0f);
    setResistance(15.0f);
    setHarvestLevel("pickaxe", 3);
  }

  @Override
  public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {

    return 0;
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {

    return Item.getItemFromBlock(this);
  }

  @Override
  public int quantityDropped(Random random) {

    return 1;
  }

  @Override
  public void addRecipes(RecipeMaker recipes) {

  }

  @Override
  public void addOreDict() {

    OreDictionary.registerOre("oreEridium", this);
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

    // TODO Auto-generated method stub
    models.put(0, new ModelResourceLocation(getFullName().toLowerCase(), "normal"));
  }
}
