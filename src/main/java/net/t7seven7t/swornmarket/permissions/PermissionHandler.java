/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.permissions;

import net.t7seven7t.swornmarket.SwornMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class PermissionHandler {
	private final SwornMarket plugin;
	
	public PermissionHandler(final SwornMarket plugin) {
		this.plugin = plugin;
	}

	public boolean hasPermission(CommandSender sender, Permission permission) {
		return (permission == null) ? true : hasPermission(sender, getPermissionString(permission));
	}

	public boolean hasPermission(CommandSender sender, String permission) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			return (p.hasPermission(permission) || p.isOp());
		}
		
		return true;
	}
	
	private String getPermissionString(Permission permission) {
		return plugin.getName().toLowerCase() + "." + permission.node.toLowerCase();
	}

}
