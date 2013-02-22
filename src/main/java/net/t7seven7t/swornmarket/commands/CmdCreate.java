/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import org.bukkit.OfflinePlayer;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;

/**
 * @author t7seven7t
 */
public class CmdCreate extends SwornMarketCommand {

	public CmdCreate(SwornMarket plugin) {
		super(plugin);
		this.name = "create";
		this.description = "Create a shop";
		this.aliases.add("c");
		this.requiredArgs.add("player");
		this.permission = Permission.CREATE_SHOP;
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		OfflinePlayer owner = getTarget(args[0]);
		if (owner == null)
			return;
		
		if (owner != player && !plugin.getPermissionHandler().hasPermission(player, Permission.CREATE_SHOP_ANY)) {
			err(plugin.getMessage("error-create-shop-others"));
			return;
		}
		
		ShopData data = plugin.getShopDataCache().newData(owner.getName(), player.getLocation());
		sendMessage(plugin.getMessage("confirm-shop-created"), owner.getName());
		plugin.getLogHandler().log(plugin.getMessage("log-shop-created"), owner.getName(), player.getName(), data.getId());		
	}

}
