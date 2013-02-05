/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import org.bukkit.Material;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.swornmarket.types.ShopItem;

/**
 * @author t7seven7t
 */
public class CmdAdd extends SwornMarketCommand {

	public CmdAdd(SwornMarket plugin) {
		super(plugin);
		this.name = "add";
		this.description = "Adds the item you are currently holding to your shop.";
		this.aliases.add("a");
		this.permission = Permission.CMD_ADD_ITEM;
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		ShopData shop = getSelectedShop();
		if (shop == null)
			return;
		
		if (!canManageShop(shop, true))
			return;
		
		if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR)) {
			err(plugin.getMessage("error-hand-empty"));
			return;
		}
		
		ShopItem item = new ShopItem(player.getItemInHand());
		shop.addItem(item);
		player.getInventory().setItemInHand(null);
		updateInventory();
		
		sendMessage(plugin.getMessage("confirm-add-item"), item.getItemStack().getAmount(), item.getName());
		plugin.getLogHandler().log(plugin.getMessage("log-add-item"), player.getName(), item.getItemStack().getAmount(), item.getName(), shop.getId());
	}

}
