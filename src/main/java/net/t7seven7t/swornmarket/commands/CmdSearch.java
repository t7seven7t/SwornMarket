/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sk89q.worldedit.blocks.ItemType;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.swornmarket.types.ShopItem;

/**
 * @author t7seven7t
 */
public class CmdSearch extends SwornMarketCommand {

	public CmdSearch(SwornMarket plugin) {
		super(plugin);
		this.name = "search";
		this.aliases.add("f");
		this.aliases.add("find");
		this.description = "Search the market for the item you are looking for";
		this.requiredArgs.add("item type");
	}

	@Override
	public void perform() {
		int itemType = getItemTypeId(args[0]);
		
		Map<ShopData, ShopItem> shopListing = new HashMap<ShopData, ShopItem>();
		for (ShopData shop : plugin.getShopDataCache().getAllShopData().values()) {
			if (shop.isOwned()) {
				for (ShopItem item : shop.getItems()) {
					if (item.getItemStack().getTypeId() == itemType) {
						shopListing.put(shop, item);
					}
				}
			}
		}
		
		sendMessage(plugin.getMessage("shop-search-header"), ItemType.toName(itemType));
		for (Entry<ShopData, ShopItem> entry : shopListing.entrySet()) {
			sendMessage(plugin.getMessage("shop-list-entry"), 
						entry.getKey().getId(), 
						entry.getKey().getAppendedName(), 
						entry.getValue().getEntry());
		}
		
		if (shopListing.isEmpty()) {
			sendMessage(plugin.getMessage("shop-page-empty"));
		}
	}

}
