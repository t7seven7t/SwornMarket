/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import org.bukkit.ChatColor;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;

/**
 * @author t7seven7t
 */
public class CmdSetName extends SwornMarketCommand {

	public CmdSetName(SwornMarket plugin) {
		super(plugin);
		this.name = "setname";
		this.description = "Change the name of your shop";
		this.permission = Permission.CMD_MODIFY_NAME;
		this.requiredArgs.add("ln1|ln2|ln3|ln4");
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		ShopData shop = getSelectedShop();
		if (shop == null)
			return;
		
		if (!canManageShop(shop, true))
			return;
		
		StringBuilder name = new StringBuilder();
		for (String s : args)
			name.append(s + " ");
		
		String[] ss = ChatColor.translateAlternateColorCodes('&', name.toString()).split("\\|");
		
		if (ss.length > 4) {
			err(plugin.getMessage("error-too-many-lines"));
			return;
		}
		
		shop.setName(ss);
		sendMessage("You have successfully changed the name of this shop to: {0}", shop.getAppendedName());
	}

}
