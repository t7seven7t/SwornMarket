/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.swornmarket.types.ShopItem;

/**
 * @author t7seven7t
 */
public class CmdDelete extends SwornMarketCommand {
	private final static List<String> deletedShopText = new ArrayList<String>();
	
	static {
		deletedShopText.add("");
		deletedShopText.add("This shop has");
		deletedShopText.add("been deleted. :(");
	}	
	
	public CmdDelete(SwornMarket plugin) {
		super(plugin);
		this.name = "delete";
		this.description = "Deletes the selected shop dropping all items.";
		this.permission = Permission.CMD_DELETE;
		this.optionalArgs.add("#shop index");
	}
	
	@Override
	public void perform() {
		ShopData data = null;
		
		if (args.length == 0 && isPlayer()) {
			data = getSelectedShop();
		} else if (args.length > 0) {
			int index = argAsInt(0, true);
			if (index < 0)
				return;
			
			data = getShop(index);
		}
		
		if (data == null)
			return;
		
		for (ShopItem item : data.getItems())
			data.getLocation().getWorld().dropItemNaturally(data.getLocation(), item.getItemStack());
		
		plugin.getShopDataCache().deleteData(data.getId());
		data.setSignText(deletedShopText);
		sendMessage(plugin.getMessage("confirm-shop-deleted"), data.getAppendedName());
		plugin.getLogHandler().log(plugin.getMessage("log-shop-deleted"), player.getName(), data.getOwner(), data.getAppendedName(), data.getId());
		plugin.getShopSelectionHandler().deselectShop(player);
	}
	
}
