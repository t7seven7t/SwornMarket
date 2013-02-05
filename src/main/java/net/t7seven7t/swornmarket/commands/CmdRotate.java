/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.swornmarket.permissions.Permission;
import net.t7seven7t.swornmarket.types.ShopData;

/**
 * @author t7seven7t
 */
public class CmdRotate extends SwornMarketCommand {

	public CmdRotate(SwornMarket plugin) {
		super(plugin);
		this.name = "rotate";
		this.description = "Rotate the currently selected shop sign.";
		this.aliases.add("r");
		this.permission = Permission.CMD_ROTATE;
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		ShopData data = getSelectedShop();
		if (data == null)
			return;
		
		data.rotateSign();
	}

}
