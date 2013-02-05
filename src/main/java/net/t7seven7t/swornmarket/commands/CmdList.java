/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.util.FormatUtil;

/**
 * @author t7seven7t
 */
public class CmdList extends PaginatedCommand {

	public CmdList(SwornMarket plugin) {
		super(plugin);
		this.name = "list";
		this.description = "Lists all shops.";
		this.optionalArgs.add("page");
		this.aliases.add("l");
	}

	@Override
	public int getListSize() {
		int max = 0;
		for (int i : plugin.getShopDataCache().getAllShopData().keySet()) {
			if (i > max)
				max = i;
		}
		return max + 1;
	}

	@Override
	public String getHeader(int index) {
		return FormatUtil.format(plugin.getMessage("shop-list-header"), index, getPageCount());
	}

	@Override
	public String getLine(int index) {
		if (plugin.getShopDataCache().getData(index) != null)
			return FormatUtil.format(	plugin.getMessage("shop-list-entry"), 
										index, 
										plugin.getShopDataCache().getData(index).getAppendedName(), 
										plugin.getShopDataCache().getData(index).getOwner());
		return null;
	}

}
