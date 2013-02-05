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
public class CmdWithdraw extends SwornMarketCommand {

	public CmdWithdraw(SwornMarket plugin) {
		super(plugin);
		this.name = "withdraw";
		this.description = "Withdraw items from your shop";
		this.aliases.add("w");
		this.permission = Permission.CMD_WITHDRAW_ITEM;
		this.requiredArgs.add("#item index/type");
		this.requiredArgs.add("amount");
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
		
		int amount = argAsInt(1, true);
		
		if (amount < 0)
			return;
		
		ItemStack stack = shop.getItems().get(index).getItemStack().clone();
		
		if (stack.getAmount() < amount) {
			err(plugin.getMessage("error-shop-stock"), amount, ItemType.toName(stack.getTypeId()));
			return;
		}
		
		stack.setAmount(amount);
		
		if (!inventoryHasRoom(stack))
			return;
		
		player.getInventory().addItem(stack);
		shop.getItems().get(index).subtractAmount(amount);

		String itemName = ItemType.toName(stack.getTypeId());
		
		sendMessage(plugin.getMessage("confirm-withdraw-item"), amount, itemName);
		plugin.getLogHandler().log(plugin.getMessage("log-remove-item"), player.getName(), amount, itemName, shop.getId());
	}

}
