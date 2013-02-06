/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.ItemType;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.Util;

/**
 * @author t7seven7t
 */
public abstract class SwornMarketCommand {
	protected final SwornMarket plugin;
	
	protected CommandSender sender;
	protected Player player;
	protected String args[];
	
	protected String name;
	protected String description;
	protected Permission permission;
	
	protected boolean mustBePlayer;
	protected List<String> requiredArgs;
	protected List<String> optionalArgs;
	protected List<String> aliases;
		
	public SwornMarketCommand(SwornMarket plugin) {
		this.plugin = plugin;
		requiredArgs = new ArrayList<String>(2);
		optionalArgs = new ArrayList<String>(2);
		aliases = new ArrayList<String>(2);
	}
	
	public abstract void perform();
	
	public final void execute(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
		if (sender instanceof Player)
			player = (Player) sender;
		
		if (mustBePlayer && !isPlayer()) {
			err(plugin.getMessage("error-must-be-player"));
			return;
		}
		
		if (requiredArgs.size() > args.length) {
			err(plugin.getMessage("error-arg-count"), getUsageTemplate(false));
			return;
		}
		
		if (hasPermission())
			perform();
		else
			err(plugin.getMessage("error-insufficient-permissions"));
	}
	
	protected final boolean isPlayer() {
		return (player != null);
	}
	
	private final boolean hasPermission() {
		return (plugin.getPermissionHandler().hasPermission(sender, permission));
	}
	
	protected final boolean argMatchesAlias(String arg, String... aliases) {
		for (String s : aliases)
			if (arg.equalsIgnoreCase(s))
				return true;
		return false;
	}
	
	protected final void err(String msg, Object... args) {
		sendMessage(plugin.getMessage("error"), FormatUtil.format(msg, args));
	}
	
	protected final void sendMessage(String msg, Object... args) {
		sender.sendMessage(ChatColor.YELLOW + FormatUtil.format(msg, args));
	}

	public final String getName() {
		return name;
	}

	public final List<String> getAliases() {
		return aliases;
	}

	public final String getUsageTemplate(final boolean displayHelp) {
		StringBuilder ret = new StringBuilder();
		ret.append("&b/shop ");
				
		ret.append(name);
		
		for (String s : aliases)
			ret.append("," + s);
		
		ret.append("&3 ");
		for (String s : requiredArgs)
			ret.append(String.format("<%s> ", s));
		
		for (String s : optionalArgs)
			ret.append(String.format("[%s] ", s));
		
		if (displayHelp)
			ret.append("&e" + description);
		
		return FormatUtil.format(ret.toString());
	}
	
	protected OfflinePlayer getTarget(String name) {
		OfflinePlayer target = Util.matchOfflinePlayer(name);
		if (target == null)
			err(plugin.getMessage("error-player-not-found"), name);
		return target;
	}
	
	protected ShopData getSelectedShop() {
		ShopData data = plugin.getShopDataCache().getData(plugin.getShopSelectionHandler().getSelectedShopKey(player));
		if (data == null)
			err(plugin.getMessage("error-no-shop-selected"));
		return data;
	}
	
	protected boolean canManageShop(ShopData data, boolean msg) {
		if (data.getOwner().equalsIgnoreCase(player.getName()))
			return true;
		
		for (String manager : data.getManagers())
			if (manager.equalsIgnoreCase(player.getName()))
				return true;
		
		if (plugin.getPermissionHandler().hasPermission(player, Permission.MANAGE_ANY_SHOP) && isBypassing())
			return true;
		
		if (msg)
			err(plugin.getMessage("error-cannot-manage"));
		return false;
	}
	
	protected boolean isBypassing() {
		return plugin.getBypassPlayers().contains(player.getName());			
	}
	
	protected ShopData getShop(int key) {
		ShopData data = plugin.getShopDataCache().getData(key);
		if (data == null)
			err(plugin.getMessage("error-no-shop-exists"), key);
		return data;
	}
	
	protected int argAsInt(int arg, boolean msg) {
		try {
			return Integer.valueOf(args[arg]);
		} catch (NumberFormatException ex) {
			if (msg)
				err(plugin.getMessage("error-invalid-syntax"), getUsageTemplate(false));
			return -1;
		}
	}
	
	protected double argAsDouble(int arg, boolean msg) {
		try {
			return Double.valueOf(args[arg]);
		} catch (NumberFormatException ex) {
			if (msg)
				err(plugin.getMessage("error-invalid-syntax"), getUsageTemplate(false));
			return -1;
		}
	}
	
	protected int argAsShopItemIndex(int arg, ShopData shop) {
		int id = -1;
		int index = argAsInt(arg, false);
		if (index < 0) {
			id = getItemTypeId(args[arg]);
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
			return -1;
		}
		
		return index;
	}
	
	@SuppressWarnings("deprecation")
	protected void updateInventory() {
		player.updateInventory();
	}
	
	protected int getAmountInInventory(ItemStack item) {
		int amount = 0;
		for (ItemStack stack : player.getInventory().getContents()) {
			if (stack != null && stack.isSimilar(item))
				amount += stack.getAmount();
		}
		return amount;
	}
	
	public boolean inventoryHasRoom(ItemStack item) {
		final int maxStackSize = (item.getMaxStackSize() == -1) ? player.getInventory().getMaxStackSize() : item.getMaxStackSize();
		int amount = item.getAmount();
		
		for (ItemStack stack : player.getInventory().getContents()) {
			if (stack == null || stack.getType().equals(Material.AIR))
				amount -= maxStackSize;			
			else if (stack.getTypeId() == item.getTypeId() && 
					stack.getDurability() == item.getDurability() &&
					(stack.getEnchantments().size() == 0 ? item.getEnchantments().size() == 0 :
						stack.getEnchantments().equals(item.getEnchantments())))
				amount -= maxStackSize - stack.getAmount();
			
			if (amount <= 0)
				return true;
		}
		
		err(plugin.getMessage("error-no-free-space"), item.getAmount(), ItemType.toName(item.getTypeId()));
		return false;
	}
	
	protected int getItemTypeId(String type) {
		ItemType item = ItemType.lookup(type);
		
		if (item == null)
			return WorldEdit.getInstance().getServer().resolveItem(type);
		
		return item.getID();
	}

}
