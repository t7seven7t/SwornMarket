/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.SimpleVector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;

/**
 * @author t7seven7t
 */
@SerializableAs("net.t7seven7t.ShopData")
public class ShopData implements ConfigurationSerializable {
	private static final int ENTRIES_PER_PAGE = 10;
	
	@Getter @Setter private int id;
	@Setter private SwornMarket plugin;
	
	@Getter private double price = -1D;
	@Getter private String owner;
	@Getter private Location location;
	@Getter private List<String> name = new ArrayList<String>();
	@Getter private List<String> managers = new ArrayList<String>();
	@Getter private List<ShopItem> items = new ArrayList<ShopItem>();
	@Getter private List<String> events = new ArrayList<String>();
	
	public ShopData(SwornMarket plugin, int id, String owner, Location location) {
		this.plugin = plugin;
		this.id = id;
		this.owner = owner;
		this.location = location;
	}
	
	public ShopData(SwornMarket plugin, int id, double price, Location location) {
		this.plugin = plugin;
		this.id = id;
		this.price = price;
		this.location = location;
	}
	
	public String getAppendedName() {
		StringBuilder ret = new StringBuilder();
		for (String s : name)
			ret.append(s + " ");
		return ret.toString();
	}
	
	public void addItem(ShopItem item) {
		for (ShopItem i : items) {
			if (i.equals(item)) {
				i.addAmount(item.getItemStack().getAmount());
				return;
			}
		}
		
		items.add(item);
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
		List<String> name = new ArrayList<String>();
		name.add("");
		name.add(owner + "'s");
		name.add("shop");
		setName(name);
	}
	
	public boolean isForSale() {
		return !isOwned();
	}
	
	public boolean isOwned() {
		return owner != null;
	}
	
	public void setName(List<String> name) {
		this.name = name;
		setSignText(name);
	}
	
	public void setName(String[] name) {
		this.name = new ArrayList<String>();
		for (String s : name)
			this.name.add(s);
		this.setSignText(this.name);
	}
	
	public void setSignText(List<String> args) {
		Block block = location.getWorld().getBlockAt(location);
		if (!(block.getState() instanceof Sign))
			block.setType(Material.SIGN_POST);
		Sign sign = (Sign) block.getState();
		int i = 0;
		for (String line : args) {
			sign.setLine(i, line);
			i++;
			if (i > 3)
				break;
		}
		sign.update();
	}
	
	public void rotateSign() {
		Block block = location.getWorld().getBlockAt(location);
		if (!(block.getState() instanceof Sign))
			block.setType(Material.SIGN_POST);
		Sign state = (Sign) block.getState();
		Directional sign = (Directional) state.getData();
		sign.setFacingDirection(getRotatedBlockFace(sign.getFacing()));
		state.update(true);
	}
	
	private BlockFace getRotatedBlockFace(BlockFace previous) {
		switch (previous) {
		case EAST:
			return BlockFace.SOUTH;
		case SOUTH:
			return BlockFace.WEST;
		case WEST:
			return BlockFace.NORTH;
		case NORTH:
		default:
			return BlockFace.EAST;
		}
	}
	
	public boolean hasSameLocation(Location location) {
		if (this.location == null)
			return false;
		return new SimpleVector(this.location).equals(new SimpleVector(location)) && location.getWorld().equals(this.location.getWorld());
	}
	
	public int getPageCount() {
		if (items.size() == 0)
			return 1;
		return (items.size() + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE;
	}
	
	public void showNextPage(Player player) {
		showPage(player, plugin.getShopSelectionHandler().getNextPage(player, this));
	}
	
	public void showPage(Player player, int index) {
		for (String line : getPage(index))
			player.sendMessage(line);
	}
	
	public String[] getPage(int index) {
		List<String> lines = new ArrayList<String>();
		
		lines.add(FormatUtil.format(plugin.getMessage("shop-page-header"), getAppendedName(), index + 1, getPageCount()));
		
		for (int i = index * 10; i < (index + 1) * 10 && i < items.size(); i++) {			
			lines.add(FormatUtil.format(plugin.getMessage("shop-page-entry"), i, items.get(i).getEntry()));
		}
		
		if (lines.size() == 1)
			lines.add(FormatUtil.format(plugin.getMessage("shop-page-empty")));
		
		return lines.toArray(new String[0]);
	}
	
	@SuppressWarnings("unchecked")
	public ShopData(Map<String, Object> args) {
		World w = Bukkit.getWorld((String) args.get("world"));
		SimpleVector v = (SimpleVector) args.get("location");
		location = new Location(w, v.x, v.y, v.z);
		name = (List<String>) args.get("name");
		
		if (args.get("owner") != null)
			owner = (String) args.get("owner");
				
		if (args.get("managers") != null)
			managers = (List<String>) args.get("managers");

		if (args.get("price") != null)
			price = (Double) args.get("price");
		
		if (args.get("items") != null)
			items = (List<ShopItem>) args.get("items");
		
		if (args.get("events") != null)
			events = (List<String>) args.get("events");
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("world", location.getWorld().getName());
		ret.put("location", new SimpleVector(location));
		ret.put("name", name);
		
		if (!managers.isEmpty())
			ret.put("managers", managers);
		if (!items.isEmpty())
			ret.put("items", items);
		if (price != -1)
			ret.put("price", price);
		if (owner != null)
			ret.put("owner", owner);
		if (!events.isEmpty())
			ret.put("events", events);
		return ret;
	}
	
}
