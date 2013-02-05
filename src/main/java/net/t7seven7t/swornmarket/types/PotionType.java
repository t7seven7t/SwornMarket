/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.types;

import java.util.Collection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author t7seven7t
 */
public enum PotionType {
	BLINDNESS("blind"),
	CONFUSION("conf"),
	DAMAGE_RESISTANCE("damage res"),
	FAST_DIGGING("haste"),
	FIRE_RESISTANCE("fire res"),
	HARM("harm"),
	HEAL("heal"),
	HUNGER("hunger"),
	INCREASE_DAMAGE("strength"),
	INVISIBILITY("invis"),
	JUMP("jump"),
	NIGHT_VISION("nv"),
	POISON("poison"),
	REGENERATION("regen"),
	SLOW("slow"),
	SLOW_DIGGING("slow dig"),
	SPEED("speed"),
	WATER_BREATHING("breathing"),
	WEAKNESS("weak"),
	WITHER("wither");
	
	public String name;
	PotionType(String name) {
		this.name = name;
	}
	
	public static String toName(PotionEffectType effect) {
		for (PotionType e : PotionType.values()) {
			if (e.toString().equals(effect.getName()))
				return e.name;
		}
		return "";
	}
	
	public static String toString(Collection<PotionEffect> effects) {
		StringBuilder result = new StringBuilder();
		for (PotionEffect effect : effects) {
			result.append(PotionType.toName(effect.getType()) + ", ");
		}
		if (result.lastIndexOf(",") >= 0) {
			result.deleteCharAt(result.lastIndexOf(","));
			result.deleteCharAt(result.lastIndexOf(" "));
		}
		return result.toString();
	}
	
}
