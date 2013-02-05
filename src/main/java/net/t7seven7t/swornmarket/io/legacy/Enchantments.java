/**
Copyright (C) 2012 t7seven7t
**/
package net.t7seven7t.swornmarket.io.legacy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;

/**
 * @author t7seven7t
 *
 */

@SerializableAs("Enchantments")
public class Enchantments implements ConfigurationSerializable {

	Map<Enchantment, Integer> enchantments;
	
	public Enchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments = enchantments;
	}
	
	public Map<Enchantment, Integer> get() {
		return enchantments;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Entry<Enchantment, Integer> entry : enchantments.entrySet())
			ret.put(entry.getKey().getName(), entry.getValue());
		return ret;
	}
	
	public static Enchantments deserialize(Map<String, Object> args) {
		Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		for (Entry<String, Object> entry : args.entrySet())
			enchantments.put(Enchantment.getByName(entry.getKey()), (Integer) entry.getValue());
		return new Enchantments(enchantments);
	}

}
