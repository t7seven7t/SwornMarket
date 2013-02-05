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
public class CmdBuy extends SwornMarketCommand {

	public CmdBuy(SwornMarket plugin) {
		super(plugin);
		this.name = "buy";
		this.description = "Buy items from the shop you're at, or set the price and amount you buy items for at your shop.";
		this.aliases.add("b");
		this.permission = Permission.CMD_BUY;
		this.mustBePlayer = true;
		this.optionalArgs.add("#item index/type");
		this.optionalArgs.add("amount");
		this.optionalArgs.add("price");
	}

	@Override
	public void perform() {
		ShopData shop = getSelectedShop();
		if (shop == null)
			return;
		
		if (args.length == 0 && shop.isForSale()) {
			double price = shop.getPrice();
			
			if (SwornMarket.getEconomy().getBalance(player.getName()) < price) {
				err(plugin.getMessage("error-funds-shop"));
				return;
			}
			
			SwornMarket.getEconomy().withdrawPlayer(player.getName(), price);
			shop.setOwner(player.getName());
			sendMessage(plugin.getMessage("confirm-shop-buy"));
			plugin.logTransaction(Level.WARNING, FormatUtil.format(plugin.getMessage("log-shop-buy"), player.getName(), shop.getId(), SwornMarket.getEconomy().format(price)));
			return;
		}
		
		if (shop.isForSale())
			return;
		
		if (args.length < 2) {
			err(plugin.getMessage("error-arg-count"), getUsageTemplate(false));
			return;
		}
		
		int id = -1;
		int index = argAsInt(0, false);
		if (index < 0) {
			id = getItemTypeId(args[0]);
			if (id > 0) {
				for (int i = 0; i < shop.getItems().size(); i++) {
					if (shop.getItems().get(i).getItemStack().getTypeId() == id) {
						index = i;
						continue;
					}
				}
			}
		}
		
		if (index >= shop.getItems().size()) {
			err(plugin.getMessage("error-no-item-with-index"), index);
			return;
		}
		
		int amount = argAsInt(1, true);
		if (amount < 0)
			return;
		
		if (canManageShop(shop, false)) {
			if (!plugin.getPermissionHandler().hasPermission(player, Permission.CMD_SET_BUY)) {
				err(plugin.getMessage("error-insufficient-permissions"));
				return;
			}
			
			ShopItem item;
			
			if (index < 0) {
				if (args.length <= 2) {
					err(plugin.getMessage("error-arg-count"), getUsageTemplate(false));
					return;
				}
				
				if (id < 0) {
					err(plugin.getMessage("error-item-not-found"), args[0]);
					return;
				}
				
				double price = argAsDouble(2, true);
				if (price < 0)
					return;
				
				item = new ShopItem(new ItemStack(id, 0), price, amount);
				shop.addItem(item);
			} else {				
				item = shop.getItems().get(index);
				
				if (args.length > 2) {
					double price = argAsDouble(2, true);
					
					if (price < 0)
						return;

					item.setBuyPrice(price);
				}
				
				item.setBuyAmount(amount);
			}
			
			sendMessage(plugin.getMessage("confirm-set-buy"), item.getBuyAmount(), item.getName(), SwornMarket.getEconomy().format(item.getBuyPrice()));
			plugin.getLogHandler().log(plugin.getMessage("log-set-buy"), player.getName(), item.getName(), item.getBuyAmount(), item.getBuyPrice(), shop.getId());
		} else {
			if (id < 0 || index < 0) {
				err(plugin.getMessage("error-item-not-found"), args[0]);
				return;
			}
			
			if (shop.getItems().get(index).getSellPrice() == 0) {
				err(plugin.getMessage("error-not-for-sale"), shop.getAppendedName(), shop.getItems().get(index).getName());
				return;
			}
			
			double price = amount * shop.getItems().get(index).getSellPrice();
			
			if (SwornMarket.getEconomy().getBalance(player.getName()) < price) {
				err(plugin.getMessage("error-funds-item"));
				return;
			}
			
			ItemStack item = shop.getItems().get(index).getItemStack().clone();
			
			if (item.getAmount() < amount) {
				err(plugin.getMessage("error-shop-stock"), amount, ItemType.toName(item.getTypeId()));
				return;
			}
			
			item.setAmount(amount);
			if (!inventoryHasRoom(item)) {
				return;
			}
			
			SwornMarket.getEconomy().withdrawPlayer(player.getName(), price);
			SwornMarket.getEconomy().depositPlayer(shop.getOwner(), price);
			
			// Don't subtract if item stock is infinite
			if (shop.getItems().get(index).getItemStack().getAmount() != -1)
				shop.getItems().get(index).subtractAmount(amount);
			
			player.getInventory().addItem(item);
			
			if (shop.getItems().get(index).getItemStack().getAmount() == 0)
				shop.getItems().remove(index);
			
			sendMessage(	plugin.getMessage("confirm-item-buy"), 
							amount, 
							shop.getItems().get(index).getName(), 
							SwornMarket.getEconomy().format(price));
			
			String event = FormatUtil.format(	plugin.getMessage("event-shop-buy"), 
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
					
			plugin.logTransaction(Level.WARNING, 
					FormatUtil.format(	plugin.getMessage("log-shop-buy"), 
										player.getName(),
										amount,
										shop.getItems().get(index).getName(),
										shop.getOwner(),
										shop.getId(),
										SwornMarket.getEconomy().format(price)));
		}
		
	}

}
