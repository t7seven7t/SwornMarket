/**
Copyright (C) 2012 t7seven7t
**/
package net.t7seven7t.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

/**
 * @author t7seven7t
 *
 */

@SerializableAs("net.t7seven7t.SimpleVector")
public class SimpleVector implements ConfigurationSerializable {

	public int x, y, z;
	
	public SimpleVector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public SimpleVector(String s) {
		String[] ss = s.split(",");
		
		this.x = Integer.parseInt(ss[0]);
		this.y = Integer.parseInt(ss[1]);
		this.z = Integer.parseInt(ss[2]);
	}
	
	public SimpleVector(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public SimpleVector(Vector v) {
		this(v.getBlockX(), v.getBlockY(), v.getBlockZ());
	}
	
	public SimpleVector(Location l) {
		this(l.toVector());
	}
	
	public String toString() {
		return (x + "," + y + "," + z);
	}
	
	public Vector toVector() {
		return new Vector(x, y, z);
	}
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		final SimpleVector that = (SimpleVector) o;
		
		if (this.x != that.x)
			return false;
		if (this.y != that.y)
			return false;
		if (this.z != that.z)
			return false;
		return true;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		result.put("c", toString());
		
		return result;
	}
	
	public static SimpleVector deserialize(Map<String, Object> args) {
		return new SimpleVector((String) args.get("c"));
	}
	
}
