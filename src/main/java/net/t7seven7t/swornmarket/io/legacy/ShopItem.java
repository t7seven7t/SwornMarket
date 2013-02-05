/**
Copyright (C) 2012 t7seven7t
**/
package net.t7seven7t.swornmarket.io.legacy;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

/**
 * @author t7seven7t
 *
 */

@SerializableAs("Item")
public class ShopItem implements ConfigurationSerializable {
	int id;
	short data;
	int amount;
	double buyPrice = 0;
	double sellPrice = 0;
	Enchantments enchantments;
	
	public ShopItem(int id, short data, Enchantments enchantments, int amount, double buyPrice, double sellPrice) {
		this.id = id;
		this.data = data;
		this.enchantments = enchantments;
		this.amount = amount;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
	}
	
	@SuppressWarnings("unchecked")
	public static ShopItem deserialize(Map<String, Object> args) {
		return new ShopItem((Integer) args.get("id"),
							Short.valueOf(String.valueOf(args.get("data"))),
							Enchantments.deserialize((Map<String, Object>) args.get("enchantments")),
							(Integer) args.get("amount"),
							(Double) args.get("buyPrice"),
							(Double) args.get("sellPrice"));
	}
	
	public net.t7seven7t.swornmarket.types.ShopItem toNewShopItem() {
		ItemStack stack = new ItemStack(id, amount, data);
		stack.addUnsafeEnchantments(enchantments.enchantments);
		return new net.t7seven7t.swornmarket.types.ShopItem(stack, buyPrice, 0, sellPrice);
	}

	public Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		return null;
	}

}
