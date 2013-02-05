/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.Util;

/**
 * @author t7seven7t
 */
public class BlockListener implements Listener {
	private final SwornMarket plugin;
	
	public BlockListener(final SwornMarket plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onSignChange(SignChangeEvent event) {
		if (!event.isCancelled() && event.getLine(0).equalsIgnoreCase("[shop]") 
				&& plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.CREATE_SHOP)) {
			double price = -1;
			try {
				price = Double.valueOf(event.getLine(1));
			} catch (NumberFormatException ex) {
				if (Util.matchOfflinePlayer(event.getLine(1)) != null) {
					String owner = Util.matchOfflinePlayer(event.getLine(1)).getName();
					if (owner != event.getPlayer().getName() && !plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.CREATE_SHOP_ANY)) {
						event.getPlayer().sendMessage(FormatUtil.format(plugin.getMessage("error-create-shop-others")));
						return;
					}
					
					ShopData data = plugin.getShopDataCache().newData(owner, event.getBlock().getLocation());
					event.getPlayer().sendMessage(FormatUtil.format(plugin.getMessage("confirm-shop-created"), owner));
					plugin.getLogHandler().log(plugin.getMessage("log-shop-created"), owner, event.getPlayer().getName(), data.getId());
					event.setCancelled(true);
					return;
				}
			}
			
			if (price <= 0) {
				event.setLine(0, ChatColor.RED + "Bad price");
				return;
			}
			
			ShopData data = plugin.getShopDataCache().newData(price, event.getBlock().getLocation());
			event.getPlayer().sendMessage(FormatUtil.format(plugin.getMessage("confirm-shop-created"), SwornMarket.getEconomy().format(price)));
			plugin.getLogHandler().log(plugin.getMessage("log-shop-created"), SwornMarket.getEconomy().format(price), event.getPlayer().getName(), data.getId());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled() ) {
			if (event.getBlock().getState() instanceof Sign) {
				if (isValidSign(event.getBlock()))
					event.setCancelled(true);
			} else {
				Block sign = event.getBlock().getRelative(BlockFace.UP);
				if (sign.getType() == Material.SIGN_POST && isValidSign(sign)) {
					event.setCancelled(true);
					return;
				}
					
				BlockFace[] directions = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH };
				for (BlockFace blockFace : directions) {
					sign = event.getBlock().getRelative(blockFace);
					if (sign.getType() == Material.WALL_SIGN) {
						org.bukkit.material.Sign si = (org.bukkit.material.Sign) sign.getState().getData();
						if (si != null && si.getFacing() == blockFace && isValidSign(sign)) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}	
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled() && event.getBlock().getState() instanceof Sign) {
			if (isValidSign(event.getBlockAgainst()))
				event.setCancelled(true);
		}	
	}
	
	public boolean isValidSign(Block block) {
		for (ShopData data : plugin.getShopDataCache().getAllShopData().values()) {
			if (data.hasSameLocation(block.getLocation())) {
				return true;
			}
		}
		return false;
	}
	
}
