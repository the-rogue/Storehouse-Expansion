/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.init;

import java.util.ArrayList;

import therogue.storehouse.init.grouped.Materials;
import therogue.storehouse.init.grouped.Resources;
import therogue.storehouse.init.grouped.Upgrades;
import therogue.storehouse.item.IStorehouseBaseItem;
import therogue.storehouse.item.MultiBlockAssembler;
import therogue.storehouse.item.NetworkInspector;
import therogue.storehouse.item.StorehouseBaseItem;

public class ModItems {
	
	/**
	 * Initialises a new array to hold all the items
	 */
	public static ArrayList<IStorehouseBaseItem> itemlist = new ArrayList<IStorehouseBaseItem>();
	
	public static void preInit () {
		/**
		 * Tools
		 */
		network_Inspector = new NetworkInspector("network_inspector");
		hammer = new StorehouseBaseItem("hammer");
		hammer.setMaxDamage(20).setNoRepair();
		multiblock_assembler = new MultiBlockAssembler("multiblock_assembler");
		itemlist.add(network_Inspector);
		itemlist.add(hammer);
		itemlist.add(multiblock_assembler);
		/*
		 * network_configurer = new NetworkConfigurer("network_configurer"); itemlist.add(network_configurer); network_requester = new NetworkRequester("network_requester"); itemlist.add(network_requester); network_tinkerer = new NetworkTinkerer("network_tinkerer"); itemlist.add(network_tinkerer);
		 * personal_stocker = new PersonalStocker("personal_stocker"); itemlist.add(personal_stocker); black_hole_starter = new BlackHoleStarter("black_hole_starter"); itemlist.add(black_hole_starter);
		 */
		itemlist.add(Resources.addMaterials());
		itemlist.add(Materials.addMaterials());
		itemlist.add(Upgrades.addMaterials());
	}
	
	public static void Init () {
		Resources.Init();
		Materials.Init();
		Upgrades.Init();
	}
	
	public static void postInit () {
	}
	
	/**
	 * Items
	 */
	public static NetworkInspector network_Inspector;
	public static StorehouseBaseItem hammer;
	public static MultiBlockAssembler multiblock_assembler;
	/*
	 * public static NetworkConfigurer network_configurer; public static NetworkRequester network_requester; public static NetworkTinkerer network_tinkerer; public static PersonalStocker personal_stocker; public static BlackHoleStarter black_hole_starter;
	 */
}
