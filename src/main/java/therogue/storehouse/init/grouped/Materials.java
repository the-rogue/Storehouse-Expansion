
package therogue.storehouse.init.grouped;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import therogue.storehouse.init.ModItems;
import therogue.storehouse.item.ItemStorehouseBaseMaterial;

public enum Materials
{
	EXTRUSION_TOOL,
	PLATE_TOOL,
	CUTTER_TOOL,
	CIRCUIT_CHIP,
	IRON_PLATE,
	COPPER_PLATE,
	TIN_PLATE,
	GOLD_PLATE,
	STEEL_PLATE,
	DIAMOND_EDGING,
	SILICON,
	INTEGRATED_CHIP,
	COPPER_WIRE,
	GOLD_WIRE,
	SOLDER,
	DIAMOND_EDGED_STEEL_PLATE,
	SINGULARITY_CORE,
	NITROGEL,
	TRANSFER_UNIT,
	FAN_BLADE,
	REFRIGERANT_PARTS;
	
	public static ItemStorehouseBaseMaterial materials;
	
	public ItemStack createStack () {
		return createStack(1);
	}
	
	public ItemStack createStack (int amount) {
		return new ItemStack(materials, amount, this.ordinal());
	}
	
	public static void addMaterials () {
		materials = new ItemStorehouseBaseMaterial("material");
		ModItems.itemlist.add(materials);
		for (Materials m : Materials.values())
			materials.addMaterial(m.ordinal(), m.name().toLowerCase());
	}
	
	public static void Init () {
		OreDictionary.registerOre("itemSilicon", SILICON.createStack());
		OreDictionary.registerOre("plateCopper", COPPER_PLATE.createStack());
		OreDictionary.registerOre("plateIron", IRON_PLATE.createStack());
		OreDictionary.registerOre("plateTin", TIN_PLATE.createStack());
		OreDictionary.registerOre("plateGold", GOLD_PLATE.createStack());
		OreDictionary.registerOre("plateSteel", STEEL_PLATE.createStack());
	}
}