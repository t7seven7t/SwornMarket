/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.types;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;

import com.sk89q.worldedit.blocks.ItemType;

/**
 * @author t7seven7t
 */
@Data
@SerializableAs("net.t7seven7t.Item")
public class ShopItem implements ConfigurationSerializable {
	private ItemStack itemStack;
	private double buyPrice;
	private double sellPrice;
	private int buyAmount;
	private boolean infinite;
	
	public ShopItem(ItemStack itemStack, double buyPrice, int buyAmount, double sellPrice) {
		this.itemStack = itemStack;
		this.buyPrice = buyPrice;
		this.buyAmount = buyAmount;
		this.sellPrice = sellPrice;
	}
	
	public ShopItem(ItemStack item, double sellPrice) {
		this(item, 0, 0, sellPrice);
	}
	
	public ShopItem(ItemStack item, double buyPrice, int buyAmount) {
		this(item, buyPrice, buyAmount, 0);
	}
	
	public ShopItem(ItemStack item) {
		this(item, 0);
	}
	
	public ShopItem(Map<String, Object> map) {
		itemStack = (ItemStack) map.get("item");
		
		if (map.get("buyPrice") != null)
			buyPrice = (Double) map.get("buyPrice");
		
		if (map.get("sellPrice") != null)
			sellPrice = (Double) map.get("sellPrice");
		
		if (map.get("buyAmount") != null)
			buyAmount = (Integer) map.get("buyAmount");
		
		if (map.get("infinite") != null)
			infinite = (Boolean) map.get("infinite");
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("item", itemStack);
		if (buyPrice != 0)
			args.put("buyPrice", buyPrice);
		if (sellPrice != 0)
			args.put("sellPrice", sellPrice);
		if (buyAmount != 0)
			args.put("buyAmount", buyAmount);
		if (infinite)
			args.put("infinite", infinite);
		return args;
	}
	
	public boolean isInfinite() {
		return infinite;
	}
	
	public void setInfinite(boolean b) {
		infinite = b;
	}
	
	public void setAmount(int amount) {
		itemStack.setAmount(amount);
	}
	
	public void addAmount(int amount) {
		itemStack.setAmount(itemStack.getAmount() + amount);
	}
	
	public void subtractAmount(int amount) {
		addAmount(-amount);
	}
	
	public String getName() {
		return ItemType.toName(itemStack.getTypeId());
	}
	
	public String getEntry() {
		StringBuilder result = new StringBuilder();
		
		result.append(itemStack.getAmount() == 0 ? "&c&o" : "&e");
		
		StringBuilder name = new StringBuilder();
		name.append(getName());
		if (itemStack.getItemMeta().getDisplayName() != null)
			name.append(" (" + ChatColor.GOLD + ChatColor.ITALIC + itemStack.getItemMeta().getDisplayName() + ChatColor.YELLOW + ") ");
		
		if (itemStack.getEnchantments().size() != 0)
			name.append("(" + EnchantmentType.toString(itemStack.getEnchantments()) + ")");
		
		if (itemStack.getItemMeta() instanceof PotionMeta) {
			Potion pot = Potion.fromItemStack(itemStack);
			name.append("(" + PotionType.toString(pot.getEffects()) + ")");
		}
		
		if (itemStack.getItemMeta() instanceof SkullMeta && ((SkullMeta) itemStack.getItemMeta()).hasOwner())
			name.append("(" + ((SkullMeta) itemStack.getItemMeta()).getOwner());
		
		result.append(String.format("%-30s", name.toString()));
		result.append("&7[" + (infinite ? "infinite" : itemStack.getAmount()) + "] ");
		result.append("&7[&6" + (sellPrice == 0 ? "-" : SwornMarket.getEconomy().format(sellPrice)));
		result.append("&7/&6" + (buyPrice == 0 ? "-" : SwornMarket.getEconomy().format(buyPrice)) + "&7]");
		return FormatUtil.format(result.toString());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		final ShopItem that = (ShopItem) o;
		
		if (!this.itemStack.isSimilar(that.itemStack))
			return false;
		return true;
	}
	
}
