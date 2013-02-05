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
public class CmdSetOwner extends SwornMarketCommand {

	public CmdSetOwner(SwornMarket plugin) {
		super(plugin);
		this.name = "setowner";
		this.description = "Set the owner of a shop.";
		this.permission = Permission.CMD_SET_OWNER;
		this.requiredArgs.add("owner");
		this.optionalArgs.add("#shop index");
	}

	@Override
	public void perform() {
		ShopData data = null;
		
		if (args.length == 1 && isPlayer()) {
			data = getSelectedShop();
		} else if (args.length > 1) {
			int index = argAsInt(1, true);
			if (index < 0)
				return;
			
			data = getShop(index);
		}
		
		if (data == null)
			return;
		
		OfflinePlayer owner = getTarget(args[0]);
		if (owner == null)
			return;
		
		data.setOwner(owner.getName());
		sendMessage(plugin.getMessage("confirm-owner-change"), data.getOwner());
		plugin.getLogHandler().log(plugin.getMessage("log-owner-change"), player.getName(), data.getId(), data.getOwner());
	}

}
