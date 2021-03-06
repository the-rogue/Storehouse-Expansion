/*
 * This file is part of Storehouse. Copyright (c) 2016, TheRogue, All rights reserved.
 * 
 * Storehouse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Storehouse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Storehouse. If not, see <http://www.gnu.org/licenses/gpl>.
 */

package therogue.storehouse.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import therogue.storehouse.LOG;

public class ServerProxy extends CommonProxy {
	
	/**
	 * PreInitialises all methods the Server needs to run, and all common methods by calling super() to common proxy
	 */
	@Override
	public void preInit (FMLPreInitializationEvent event) {
		super.preInit(event);
		LOG.debug("Server Proxy Started PreInitialisation");
		LOG.debug("Server Proxy Finished PreInitialisation");
	}
	
	/**
	 * Initialises all methods the Server needs to run, and all common methods by calling super() to common proxy
	 */
	@Override
	public void init (FMLInitializationEvent event) {
		super.init(event);
		LOG.debug("Server Proxy Started Initialisation");
		LOG.debug("Server Proxy Finished Initialisation");
	}
	
	/**
	 * PostInitialises all methods the Server needs to run, and all common methods by calling super() to common proxy
	 */
	@Override
	public void postInit (FMLPostInitializationEvent event) {
		super.postInit(event);
		LOG.debug("Server Proxy Started PostInitialisation");
		LOG.debug("Server Proxy Finished PostInitialisation");
	}
}
