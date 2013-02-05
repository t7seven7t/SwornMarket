/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.permissions;

/**
 * @author t7seven7t
 */
public enum Permission {
	CMD_ADD_ITEM("cmd.item.add"),
	CMD_BUY("cmd.buy"),
	CMD_DELETE("cmd.delete"),
	CMD_INFINITE("cmd.infinite"),
	CMD_MANAGER("cmd.manager"),
	CMD_MODIFY_NAME("cmd.modifyname"),
	CMD_REMOVE_ITEM("cmd.item.remove"),
	CMD_ROTATE("cmd.rotate"),
	CMD_SELL("cmd.sell"),
	CMD_SET("cmd.set"),
	CMD_SET_BUY("cmd.set.buy"),
	CMD_SET_OWNER("cmd.setowner"),
	CMD_SET_SELL("cmd.set.sell"),
	CMD_TELEPORT("cmd.teleport"),
	CMD_WITHDRAW_ITEM("cmd.item.withdraw"),
	CREATE_SHOP("create"),
	CREATE_SHOP_ANY("create.any"),
	MANAGE_ANY_SHOP("manage.any");
	
	final String node;
	Permission(final String node) {
		this.node = node;
	}
}
