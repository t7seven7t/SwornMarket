/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.types.ShopData;

/**
 * @author t7seven7t
 */
public class CmdPage extends SwornMarketCommand {

	public CmdPage(SwornMarket plugin) {
		super(plugin);
		this.name = "page";
		this.description = "Show a specific page of the shop selected";
		this.aliases.add("p");
		this.mustBePlayer = true;
		this.requiredArgs.add("#page index");
	}

	@Override
	public void perform() {
		ShopData shop = getSelectedShop();
		if (shop == null || shop.isForSale())
			return;
		
		int index = argAsInt(0, true);
		if (index < 0)
			return;
		
		if (index < 1 || index > shop.getPageCount()) {
			err(plugin.getMessage("error-invalid-page"), args[0]);
			return;
		}
		
		shop.showPage(player, index - 1);
	}

}
