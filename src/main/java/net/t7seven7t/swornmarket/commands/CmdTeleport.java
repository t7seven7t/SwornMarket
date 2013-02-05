/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;

/**
 * @author t7seven7t
 */
public class CmdTeleport extends SwornMarketCommand {

	public CmdTeleport(SwornMarket plugin) {
		super(plugin);
		this.name = "teleport";
		this.description = "Teleport to a shop.";
		this.permission = Permission.CMD_TELEPORT;
		this.aliases.add("tp");
		this.requiredArgs.add("#shop index");
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform() {
		int index = argAsInt(0, true);
		if (index < 0)
			return;
		
		ShopData data = getShop(index);
		if (data == null)
			return;
		
		player.teleport(data.getLocation());
		sendMessage(plugin.getMessage("confirm-teleport"));
	}
	
}
