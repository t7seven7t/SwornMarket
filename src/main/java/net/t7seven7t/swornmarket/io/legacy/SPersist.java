package net.t7seven7t.swornmarket.io.legacy;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

@Deprecated
public class SPersist {
	
	public static <T> void load(T instance, Class<? extends T> clazz, File file) {		
		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
		
		for (Field f : clazz.getDeclaredFields()) {
			if (!Modifier.isTransient(f.getModifiers())) {
				if (fc.get(f.getName()) != null) {
					f.setAccessible(true);
					try {
						f.set(instance, fc.get(f.getName()));
					} catch (Throwable e) {
					}
				}
			}
		}
	}
	
}
