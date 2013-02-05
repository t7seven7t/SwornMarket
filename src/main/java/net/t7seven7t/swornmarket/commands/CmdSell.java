/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.blocks.ItemType;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.swornmarket.types.ShopItem;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.Util;

/**
 * @author t7seven7t
 */
public class CmdSell extends SwornMarketCommand {

	public CmdSell(SwornMarket plugin) {
		super(plugin);
		this.name = "sell";
		this.description = "Sell items to the shop you're at.";
		this.aliases.add("s");
		this.permission = Permission.CMD_SELL;
		this.mustBePlayer = true;
		this.requiredArgs.add("#item index/type");
		this.requiredArgs.add("amount");
	}

	@Override
	public void perform() {
		ShopData shop = getSelectedShop();
		if (shop == null)
			return;
		
		if (shop.isForSale())
			return;
		
		int index = argAsShopItemIndex(0, shop);
		
		if (index < 0)
			return;
		
		if (canManageShop(shop, false)) {
			if (!plugin.getPermissionHandler().hasPermission(player, Permission.CMD_SET_SELL)) {
				err(plugin.getMessage("error-insufficient-permissions"));
				return;
			}
			
			ShopItem item;
			
			double price = argAsDouble(1, true);
			
			if (price < 0)
				return;
			
			item = shop.getItems().get(index);
			
			item.setSellPrice(price);
			
			sendMessage(plugin.getMessage("confirm-set-sell"), item.getName(), SwornMarket.getEconomy().format(item.getSellPrice()));
			plugin.getLogHandler().log(plugin.getMessage("log-set-sell"), player.getName(), item.getName(), item.getSellPrice(), shop.getId());
		} else {
			int amount = argAsInt(1, true);
			if (amount < 0)
				return;
			
			if (shop.getItems().get(index).getBuyPrice() == 0) {
				err(plugin.getMessage("error-not-buying"), shop.getAppendedName(), shop.getItems().get(index).getName());
				return;
			}
			
			if (shop.getItems().get(index).getBuyAmount() < amount) {
				err(plugin.getMessage("error-not-buying-that-much"), shop.getAppendedName(), shop.getItems().get(index).getBuyAmount(), shop.getItems().get(index).getName());
				return;
			}
			
			double price = amount * shop.getItems().get(index).getBuyPrice();
			
			if (SwornMarket.getEconomy().getBalance(shop.getOwner()) < price) {
				err(plugin.getMessage("error-funds-owner"));
				return;
			}
			
			ItemStack item = shop.getItems().get(index).getItemStack().clone();
			item.setAmount(amount);
			if (!player.getInventory().containsAtLeast(item, amount)) {
				err(plugin.getMessage("error-item-stock"), amount, ItemType.toName(item.getTypeId()));
				return;
			}
			
			SwornMarket.getEconomy().withdrawPlayer(shop.getOwner(), price);
			SwornMarket.getEconomy().depositPlayer(player.getName(), price);
			player.getInventory().removeItem(item);
			
			// Don't add if item stock is infinite.
			if (shop.getItems().get(index).getItemStack().getAmount() != -1)
				shop.getItems().get(index).addAmount(amount);
			
			sendMessage(plugin.getMessage("confirm-item-sell"), amount, shop.getItems().get(index).getName(), SwornMarket.getEconomy().format(price));
			
			String event = FormatUtil.format(	plugin.getMessage("event-shop-sell"), 
												player.getName(),
												amount,
												shop.getItems().get(index).getName(),
												SwornMarket.getEconomy().format(price));
			
			OfflinePlayer owner = Util.matchOfflinePlayer(shop.getOwner());
			if (owner != null && owner.isOnline()) {
				((Player) owner).sendMessage(event);
			} else {
				shop.getEvents().add(event);
			}

			plugin.logTransaction(Level.INFO, 
					FormatUtil.format(	plugin.getMessage("log-shop-sell"), 
										player.getName(),
										amount,
										shop.getItems().get(index).getName(),
										shop.getOwner(),
										shop.getId(),
										SwornMarket.getEconomy().format(price)));
		}
		
	}

}
