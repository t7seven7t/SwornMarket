/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.io.legacy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.io.FileSerialization;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.util.LogHandler;

/**
 * @author t7seven7t
 */
@SuppressWarnings("deprecation")
public class ShopDataConverter {

	public static void run(SwornMarket plugin) {
		LogHandler logger = plugin.getLogHandler();
		logger.log("Starting shop data conversion...");
		long start = System.currentTimeMillis();
		
		File folder = new File(plugin.getDataFolder(), "shops");
		
		for (File file : folder.listFiles()) {
			Shop oldData = new Shop();
			SPersist.load(oldData, Shop.class, file);
						
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("world", oldData.world);
			data.put("location", new net.t7seven7t.util.SimpleVector(oldData.location.x, oldData.location.y, oldData.location.z));
			data.put("name", oldData.name);
			data.put("owner", oldData.owner);
			
			List<net.t7seven7t.swornmarket.types.ShopItem> newItems = new ArrayList<net.t7seven7t.swornmarket.types.ShopItem>();
			for (ShopItem item : oldData.items)
				newItems.add(item.toNewShopItem());
			
			data.put("items", newItems);
			
			ShopData newData = (ShopData) ConfigurationSerialization.deserializeObject(data, ShopData.class);
			FileSerialization.save(newData, new File(folder, file.getName() + ".dat"));
			file.delete();
		}
		
		logger.log("Old shop data converted! Took {0}ms", System.currentTimeMillis() - start);
	}
	
}
