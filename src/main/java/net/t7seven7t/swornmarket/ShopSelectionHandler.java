/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket;

import java.util.HashMap;
import java.util.Map;

import net.t7seven7t.swornmarket.types.ShopData;

import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class ShopSelectionHandler {
	private Map<String, Map<Integer, Integer>> playerShopPageIndex;
	private Map<String, Integer> playerShopSelection;
	
	public ShopSelectionHandler() {		
		playerShopPageIndex = new HashMap<String, Map<Integer, Integer>>();
		playerShopSelection = new HashMap<String, Integer>();
	}
	
	public int getNextPage(Player player, ShopData shop) {
		String name = player.getName();
		
		if (playerShopPageIndex.get(name) == null)
			playerShopPageIndex.put(name, new HashMap<Integer, Integer>());
		
		if (playerShopPageIndex.get(name).get(shop.getId()) == null) {
			playerShopPageIndex.get(name).put(shop.getId(), 0);
			return 0;
		}
		
		int pageIndex = playerShopPageIndex.get(name).get(shop.getId()) + 1;
		if (pageIndex >= shop.getPageCount())
			pageIndex = 0;
		
		playerShopPageIndex.get(name).put(shop.getId(), pageIndex);
		
		return pageIndex;
	}
	
	public void selectShop(Player player, int key) {
		if (playerShopSelection.containsKey(player.getName()))
			playerShopSelection.remove(player.getName());
		playerShopSelection.put(player.getName(), key);
	}
	
	public void deselectShop(Player player) {
		playerShopSelection.remove(player.getName());
	}
	
	public int getSelectedShopKey(Player player) {
		return playerShopSelection.get(player.getName()) == null ? -1 : playerShopSelection.get(player.getName());
	}
	
}
