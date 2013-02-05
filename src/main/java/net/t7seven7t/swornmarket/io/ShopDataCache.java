/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.types.ShopData;

/**
 * @author t7seven7t
 */
public class ShopDataCache {
	private final SwornMarket plugin;
	private final File folder;
	private final String extension = ".dat";
	private final String folderName = "shops";
	private final Object readWriteLock = new Object();
	private final Object mapLock = new Object();
	
	private Map<Integer, ShopData> data;
	
	public ShopDataCache(final SwornMarket plugin) {
		this.plugin = plugin;
		this.folder = new File(plugin.getDataFolder(), folderName);
		
		if (!folder.exists())
			folder.mkdir();
		
		this.data = new HashMap<Integer, ShopData>();
		loadAllShopData();
	}
	
	private int getFreeKey(final int start) {
		int key = recurseFreeKey(start);
		int newKey;
		
		// Iterate through until we find an id that definitely does not already exist.
		while (key != (newKey = recurseFreeKey(key)))
			key = newKey;
		
		return key;
	}
	
	private int recurseFreeKey(final int start) {
		int key = start;
		for (int i : data.keySet())
			if (key == i)
				key++;
		return key;
	}
	
	private void loadAllShopData() {
		for (File file : folder.listFiles())
			if (!isFileAlreadyLoaded(file))
				data.put(getKeyFromFileName(file), loadData(file));
	}
	
	public ShopData getData(final int key) {
		return data.get(key);
	}
	
	// Useful for searching for shops owned by a player
	public ShopData getData(final OfflinePlayer player) {
		for (ShopData value : data.values())
			if (value.getOwner().equals(player.getName()))
				return value;
		return null;
	}
	
	public Map<Integer, ShopData> getAllShopData() {
		return Collections.unmodifiableMap(data);
	}
	
	public void deleteData(final int key) {
		removeData(key);
		new File(folder, getFileName(key)).delete();
	}
	
	private void removeData(final int key) {
		synchronized(mapLock) {
			Map<Integer, ShopData> copy = new HashMap<Integer, ShopData>();
			copy.putAll(data);
			copy.remove(key);
			data = Collections.unmodifiableMap(copy);
		}
	}
	
	private void addData(final int key, final ShopData value) {
		synchronized(mapLock) {
			Map<Integer, ShopData> copy = new HashMap<Integer, ShopData>();
			copy.putAll(data);
			copy.put(key, value);
			data = Collections.unmodifiableMap(copy);
		}
	}
	
	public ShopData newData(String owner, Location location) {
		int key = getFreeKey(0);
		ShopData value = new ShopData(plugin, key, owner, location);
		addData(key, value);
		List<String> name = new ArrayList<String>();
		name.add("");
		name.add(owner + "'s");
		name.add("shop");
		value.setName(name);
		return value;
	}
	
	public ShopData newData(double price, Location location) {
		int key = getFreeKey(0);
		ShopData value = new ShopData(plugin, key, price, location);
		addData(key, value);
		List<String> name = new ArrayList<String>();
		name.add("");
		name.add("For sale:");
		name.add(SwornMarket.getEconomy().format(price));
		value.setName(name);
		return value;
	}
	
	private void cleanupData() {
		for (Entry<Integer, ShopData> entry : getAllShopData().entrySet()) {
			if (entry.getValue() == null || entry.getValue().getLocation() == null) {
				plugin.getLogHandler().log("Deleting shop with id {0} because it has null data.", entry.getKey());
				deleteData(entry.getKey());
				continue;
			}
			
			ShopData data = entry.getValue();
			if (!(data.getLocation().getWorld().getBlockAt(data.getLocation()).getState() instanceof Sign)) {
				data.setSignText(data.getName());
			}
		}
	}
	
	private ShopData loadData(final File file) {
		synchronized(readWriteLock) {
			ShopData value = FileSerialization.load(file, ShopData.class);
			value.setId(getKeyFromFileName(file));
			value.setPlugin(plugin);
			return value;
		}
	}
	
	public void save() {
		synchronized(readWriteLock) {
			plugin.getLogHandler().log("Saving {0} to disk...", folderName);
			long start = System.currentTimeMillis();
			cleanupData();
			for (Entry<Integer, ShopData> entry : getAllShopData().entrySet())
				synchronized(entry.getValue()) {
					FileSerialization.save(entry.getValue(), new File(folder, getFileName(entry.getKey())));
				}
			plugin.getLogHandler().log("{0} saved! [{1}ms]", folderName, System.currentTimeMillis() - start);
		}
	}
	
	private boolean isFileAlreadyLoaded(final File file) {
		int key = getKeyFromFileName(file);
		for (int k : data.keySet())
			if (k == key)
				return true;
		return false;
	}
	
	private int getKeyFromFileName(final File file) {
		int index = file.getName().lastIndexOf(extension);
		return Integer.valueOf(index > 0 ? file.getName().substring(0, index) : file.getName());
	}
	
	private String getFileName(final int key) {
		return key + extension;
	}
}
