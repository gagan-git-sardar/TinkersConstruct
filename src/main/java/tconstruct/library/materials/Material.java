package tconstruct.library.materials;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.client.MaterialRenderInfo;
import tconstruct.library.mantle.RecipeMatch;
import tconstruct.library.mantle.RecipeMatchRegistry;
import tconstruct.library.traits.ITrait;

public class Material {

  public static final Material UNKNOWN = new Material();
  public static final String LOCALIZATION_STRING = "material.%s.name";

  /**
   * This String uniquely identifies a material.
   */
  @Nonnull
  public final String identifier;

  /**
   * Items associated with this material. Used for repairing and identifying items that belong to a material.
   */
  protected RecipeMatchRegistry materialItems;

  /** The fluid associated with this material */
  protected Fluid fluid;

  /**
   * How the material will be rendered on tinker tools etc.
   */
  public final MaterialRenderInfo renderInfo;

  public final EnumChatFormatting textColor; // used in tooltips and other text

  /**
   * This item, if it is not null, represents the material for rendering.
   * In general if you want to give a person this material, you can give them this item.
   */
  private ItemStack representativeItem;


  // we use a Treemap for 2 reasons:
  // * A Map so we can obtain the stats we want quickly
  // * A treemap because we can sort it, so that all materials have the same order when iterating
  protected final Map<String, IMaterialStats> stats = new TreeMap<>();
  protected final Map<String, ITrait> traits = new TreeMap<>();

  private Material() {
    this.identifier = "Unknown";
    this.renderInfo = new MaterialRenderInfo.Default(0xffffff);
    this.textColor = EnumChatFormatting.WHITE;
  }

  // simple white material
  public Material(String identifier) {
    this(identifier, 0xffffff, EnumChatFormatting.GRAY);
  }

  // one-colored material
  public Material(String identifier, int color, EnumChatFormatting textColor) {
    this(identifier, new MaterialRenderInfo.Default(color), textColor);
  }

  // multi-colored material
  public Material(String identifier, int colorLow, int colorMid, int colorHigh,
                  EnumChatFormatting textColor) {
    this(identifier, new MaterialRenderInfo.Default(colorLow, colorMid, colorHigh), textColor);
  }

  // complex material with 3 colors and a real surface texture!
  public Material(String identifier, MaterialRenderInfo renderInfo, EnumChatFormatting textColor) {
    this.identifier = identifier;
    this.renderInfo = renderInfo;
    this.textColor = textColor;
  }

  /* Stats */

  /**
   * Do not use this function directly stats. Use TinkerRegistry.addMaterialStats instead.
   */
  public void addStats(IMaterialStats materialStats) {
    this.stats.put(materialStats.getMaterialType(), materialStats);
  }

  /**
   * Returns the given type of stats if the material has them. Returns null Otherwise.
   */
  private IMaterialStats getStatsSafe(String identifier) {
    if(identifier == null || identifier.isEmpty()) {
      return null;
    }

    for(IMaterialStats stat : stats.values()) {
      if(identifier.equals(stat.getMaterialType())) {
        return stat;
      }
    }

    return null;
  }

  /**
   * Returns the material stats of the given type of this material.
   *
   * @param identifier Identifier of the material.
   * @param <T>        Type of the Stats are determined by return value. Use the correct
   * @return The stats found or null if none present.
   */
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> T getStats(String identifier) {
    return (T) getStatsSafe(identifier);
  }

  public Collection<IMaterialStats> getAllStats() {
    return stats.values();
  }

  public boolean hasStats(String identifier) {
    return getStats(identifier) != null;
  }

  /* Traits */

  /**
   * Do not use this function with unregistered traits. Use TinkerRegistry.addMaterialTrait instead.
   */
  public void addTrait(ITrait materialTrait) {
    // rgister unregistered traits
    if(TinkerRegistry.getTrait(materialTrait.getIdentifier()) == null) {
      TinkerRegistry.addTrait(materialTrait);
    }
    this.traits.put(materialTrait.getIdentifier(), materialTrait);
  }

  /**
   * Returns whether the material has a trait with that identifier.
   */
  public boolean hasTrait(String identifier) {
    if(identifier == null || identifier.isEmpty()) {
      return false;
    }

    return traits.containsKey(identifier);
  }

  public Collection<ITrait> getAllTraits() {
    return this.traits.values();
  }

  /* Data about the material itself */

  public boolean hasFluid() {
    return fluid != null;
  }

  public Fluid getFluid() {
    return fluid;
  }

  public void setFluid(Fluid fluid) {
    if(!FluidRegistry.isFluidRegistered(fluid)) {
      TinkerRegistry.log.warn("Materials cannot have an unregistered fluid associated with them!");
    }
    this.fluid = fluid;
  }

  public RecipeMatch.Match matches(ItemStack... stacks) {
    return materialItems.matches(stacks);
  }

  public void addItem(String oredictItem) {
    materialItems.addItem(oredictItem);
  }

  public void addItem(Item item) {
    materialItems.addItem(item);
  }

  public void addItem(Block block, int count) {
    materialItems.addItem(block, count);
  }

  public RecipeMatchRegistry getItemRegistry() {
    return materialItems;
  }

  public void setRepresentativeItem(ItemStack representativeItem) {
    if(representativeItem == null) {
      this.representativeItem = null;
    }
    else if(matches(representativeItem) != null) {
      this.representativeItem = representativeItem;
    }
    else {
      TinkerRegistry.log.warn("Itemstack {} cannot represent material {} since it is not associated with the material!",
                              representativeItem.toString(),
                              identifier);
    }
  }

  public ItemStack getRepresentativeItem() {
    return representativeItem;
  }

  public String getLocalizedName() {
    return StatCollector
        .translateToLocal(String.format(LOCALIZATION_STRING, Util.sanitizeLocalizationString(identifier)));
  }
}
