/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.blocks.ItemType;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;

/**
 * @author t7seven7t
 */
public class CmdRemove extends SwornMarketCommand {

	public CmdRemove(SwornMarket plugin) {
		super(plugin);
		this.name = "remove";
		this.aliases.add("r");
		this.description = "Removes items from your shop and give them back to you";
		this.permission = Permission.CMD_REMOVE_ITEM;
		this.requiredArgs.add("#item index/type");
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		ShopData shop = getSelectedShop();
		if (shop == null)
			return;
		
		if (!canManageShop(shop, true))
			return;
		
		int index = argAsShopItemIndex(0, shop);
		
		if (index < 0)
			return;
		
		ItemStack stack = shop.getItems().get(index).getItemStack();

		if (!inventoryHasRoom(stack))
			return;
		
		player.getInventory().addItem(stack);
		shop.getItems().remove(index);
		
		String itemName = ItemType.toName(stack.getTypeId());
		
		sendMessage(plugin.getMessage("confirm-remove-item"), itemName);
		plugin.getLogHandler().log(plugin.getMessage("log-remove-item"), player.getName(), stack.getAmount(), itemName, shop.getId());
	}
	
}
