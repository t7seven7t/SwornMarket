/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;

/**
 * @author t7seven7t
 */
public class CmdBypass extends SwornMarketCommand {

	public CmdBypass(SwornMarket plugin) {
		super(plugin);
		this.name = "bypass";
		this.description = "Use to bypass normal controls to manage shops.";
		this.permission = Permission.MANAGE_ANY_SHOP;
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		if (plugin.getBypassPlayers().contains(player.getName())) {
			plugin.getBypassPlayers().remove(player.getName());
			sendMessage("You are no longer bypassing.");
		} else {
			plugin.getBypassPlayers().add(player.getName());
			sendMessage("You are now bypassing.");
		}
	}

}
