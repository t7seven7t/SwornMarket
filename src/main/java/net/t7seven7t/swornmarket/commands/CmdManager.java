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
public class CmdManager extends SwornMarketCommand {

	public CmdManager(SwornMarket plugin) {
		super(plugin);
		this.name = "manager";
		this.description = "Add someone to manage your shop.";
		this.mustBePlayer = true;
		this.permission = Permission.CMD_MANAGER;
		this.requiredArgs.add("player");
	}

	@Override
	public void perform() {
		ShopData shop = getSelectedShop();
		if (shop == null)
			return;
		
		if (!shop.getOwner().equalsIgnoreCase(player.getName()) && !plugin.getPermissionHandler().hasPermission(player, Permission.MANAGE_ANY_SHOP)) {
			err(plugin.getMessage("error-cannot-manage"));
			return;
		}
		
		OfflinePlayer manager = getTarget(args[0]);
		
		if (manager != null) {
			if (shop.getManagers().contains(manager.getName())) {
				shop.getManagers().remove(manager.getName());
				sendMessage(plugin.getMessage("confirm-manager-demote"), manager.getName());
			} else {
				shop.getManagers().add(manager.getName());
				sendMessage(plugin.getMessage("confirm-manager-promote"), manager.getName());
			}
		}
	}

}
