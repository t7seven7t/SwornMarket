/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.swornmarket.types.ShopItem;

/**
 * @author t7seven7t
 */
public class CmdInfinite extends SwornMarketCommand {

	public CmdInfinite(SwornMarket plugin) {
		super(plugin);
		this.name = "infinite";
		this.description = "Makes the stock of this item infinite in this shop";
		this.permission = Permission.CMD_INFINITE;
		this.requiredArgs.add("#item index/type");
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		ShopData shop = getSelectedShop();
		if (shop == null)
			return;
		
		if (!canManageShop(shop, true))
			return;
		
		int index = argAsShopItemIndex(0, shop);
		
		if (index < 0)
			return;
		
		ShopItem item = shop.getItems().get(index);
		
		item.setAmount(-1);
		
		sendMessage(plugin.getMessage("confirm-item-infinite"), item.getName());
		plugin.getLogHandler().log(plugin.getMessage("log-item-infinite"), player.getName(), item.getName(), shop.getId());
	}

}
