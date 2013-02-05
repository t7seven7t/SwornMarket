/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.listeners;

import java.util.Collections;
import java.util.List;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author t7seven7t
 */
public class PlayerListener implements Listener {
	private final SwornMarket plugin;
	
	public PlayerListener(final SwornMarket plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (	event.getAction().equals(Action.RIGHT_CLICK_BLOCK) 
				&& event.getClickedBlock().getState() instanceof Sign) {
			
			for (ShopData data : plugin.getShopDataCache().getAllShopData().values()) {
				
				if (data.hasSameLocation(event.getClickedBlock().getLocation())) {
					
					if (data.isForSale()) {
						event.getPlayer().sendMessage(FormatUtil.format(plugin.getMessage("shop-for-sale")));
					} else {
						data.showNextPage(event.getPlayer());
					}
					
					// Select shop
					plugin.getShopSelectionHandler().selectShop(event.getPlayer(), data.getId());
					
					return;
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		if (	!event.isCancelled() 
				&& plugin.getShopSelectionHandler().getSelectedShopKey(event.getPlayer()) >= 0) {
			
			ShopData data = plugin.getShopDataCache().getData(plugin.getShopSelectionHandler().getSelectedShopKey(event.getPlayer()));
			
			if (	data.isOwned()
					&& (data.getLocation().getWorld() != event.getPlayer().getWorld() 
					|| data.getLocation().distanceSquared(event.getPlayer().getLocation()) > 16)) {
				
				event.getPlayer().sendMessage(FormatUtil.format(plugin.getMessage("leave-shop"), data.getAppendedName()));
				plugin.getShopSelectionHandler().deselectShop(event.getPlayer());
				
			}
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (ShopData shop : plugin.getShopDataCache().getAllShopData().values()) {
			if (shop.isOwned() && shop.getOwner().equalsIgnoreCase(event.getPlayer().getName())) {
				List<String> events = Collections.unmodifiableList(shop.getEvents());
				for (int i = 0; i < events.size(); i++) {
					event.getPlayer().sendMessage(events.get(i));
					shop.getEvents().remove(i);
				}
				
				return;
			}
		}
	}
}
