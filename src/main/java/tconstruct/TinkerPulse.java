package tconstruct;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

import tconstruct.library.Util;

/**
 * Just a small helper class that provides some function for cleaner Pulses.
 *
 * Items should be registered during PreInit
 *
 * Models should be registered during Init
 */
public abstract class TinkerPulse {

  /**
   * Sets the correct unlocalized name and registers the item.
   */
  protected static <T extends Item> T registerItem(T item, String unlocName) {
    item.setUnlocalizedName(Util.prefix(unlocName));
    GameRegistry.registerItem(item, unlocName);
    return item;
  }

  protected static <T extends Block> T registerBlock(T block, String unlocName) {
    block.setUnlocalizedName(Util.prefix(unlocName));
    GameRegistry.registerBlock(block, unlocName);
    return block;
  }

  protected static <T extends Block> T registerBlock(T block,
                                                     Class<? extends ItemBlock> itemBlockClazz,
                                                     String unlocName) {
    block.setUnlocalizedName(Util.prefix(unlocName));
    GameRegistry.registerBlock(block, itemBlockClazz, unlocName);
    return block;
  }
}
