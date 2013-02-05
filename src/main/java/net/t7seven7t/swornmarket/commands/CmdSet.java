/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.swornmarket.types.ShopItem;

/**
 * @author t7seven7t
 */
public class CmdSet extends SwornMarketCommand {

	public CmdSet(SwornMarket plugin) {
		super(plugin);
		this.name = "set";
		this.description = "Set the buy/sell price for items.";
		this.permission = Permission.CMD_SET;
		this.mustBePlayer = true;
		this.requiredArgs.add("#item index/type");
		this.requiredArgs.add("buy,b/sell,s");
		this.requiredArgs.add("price");
		this.optionalArgs.add("buy amount");
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
		
		ShopItem item = shop.getItems().get(index);
		
		double price = argAsDouble(2, true);
		
		if (price < 0)
			return;
		
		if (argMatchesAlias(args[1], "buy", "b")) {
			if (!plugin.getPermissionHandler().hasPermission(player, Permission.CMD_SET_BUY)) {
				err(plugin.getMessage("error-insufficient-permissions"));
				return;
			}
			
			if (args.length > 3) {
				int buyAmount = argAsInt(3, true);
				if (buyAmount < 0)
					return;
				
				item.setBuyAmount(buyAmount);
			}
			
			item.setBuyPrice(price);
			sendMessage(plugin.getMessage("confirm-set-buy"), item.getBuyAmount(), item.getName(), SwornMarket.getEconomy().format(item.getBuyPrice()));
			plugin.getLogHandler().log(plugin.getMessage("log-set-buy"), player.getName(), item.getName(), item.getBuyAmount(), item.getBuyPrice(), shop.getId());
			return;
		}
		
		if (argMatchesAlias(args[1], "sell", "s")) {
			if (!plugin.getPermissionHandler().hasPermission(player, Permission.CMD_SET_SELL)) {
				err(plugin.getMessage("error-insufficient-permissions"));
				return;
			}
			
			item.setSellPrice(price);
			sendMessage(plugin.getMessage("confirm-set-sell"), item.getName(), SwornMarket.getEconomy().format(item.getSellPrice()));
			plugin.getLogHandler().log(plugin.getMessage("log-set-sell"), player.getName(), item.getName(), item.getSellPrice(), shop.getId());
			return;
		}
		
		err(plugin.getMessage("error-invalid-syntax"), getUsageTemplate(false));
	}

}
