/**
Copyright (C) 2012 t7seven7t
**/
package net.t7seven7t.swornmarket.io.legacy;

import java.util.List;

/**
 * @author t7seven7t
 *
 */

public class Shop {
	public final static int NUM_ENTRIES_PER_PAGE = 10;
	
	List<String> name;
	String owner;
	List<String> managers;
	SimpleVector location;
	String world;
	List<ShopItem> items;
	
	public Shop() {
		
	}

}
