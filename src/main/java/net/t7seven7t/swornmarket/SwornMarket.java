/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;

import net.milkbowl.vault.economy.Economy;
import net.t7seven7t.swornmarket.commands.CmdAdd;
import net.t7seven7t.swornmarket.commands.CmdAddAll;
import net.t7seven7t.swornmarket.commands.CmdBuy;
import net.t7seven7t.swornmarket.commands.CmdBypass;
import net.t7seven7t.swornmarket.commands.CmdCreate;
import net.t7seven7t.swornmarket.commands.CmdDelete;
import net.t7seven7t.swornmarket.commands.CmdHelp;
import net.t7seven7t.swornmarket.commands.CmdInfinite;
import net.t7seven7t.swornmarket.commands.CmdList;
import net.t7seven7t.swornmarket.commands.CmdManager;
import net.t7seven7t.swornmarket.commands.CmdPage;
import net.t7seven7t.swornmarket.commands.CmdRemove;
import net.t7seven7t.swornmarket.commands.CmdRotate;
import net.t7seven7t.swornmarket.commands.CmdSell;
import net.t7seven7t.swornmarket.commands.CmdSet;
import net.t7seven7t.swornmarket.commands.CmdSetName;
import net.t7seven7t.swornmarket.commands.CmdSetOwner;
import net.t7seven7t.swornmarket.commands.CmdTeleport;
import net.t7seven7t.swornmarket.commands.CmdWithdraw;
import net.t7seven7t.swornmarket.commands.CommandHandler;
import net.t7seven7t.swornmarket.io.ResourceHandler;
import net.t7seven7t.swornmarket.io.ShopDataCache;
import net.t7seven7t.swornmarket.io.legacy.ShopDataConverter;
import net.t7seven7t.swornmarket.listeners.BlockListener;
import net.t7seven7t.swornmarket.listeners.PlayerListener;
import net.t7seven7t.swornmarket.permissions.PermissionHandler;
import net.t7seven7t.swornmarket.types.ShopData;
import net.t7seven7t.swornmarket.types.ShopItem;
import net.t7seven7t.util.LogHandler;
import net.t7seven7t.util.SimpleVector;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class SwornMarket extends JavaPlugin {
	private @Getter LogHandler logHandler;
	private @Getter CommandHandler commandHandler;
	private @Getter PermissionHandler permissionHandler;
	private @Getter ResourceHandler resourceHandler;
	private @Getter ShopDataCache shopDataCache;
	private @Getter ShopSelectionHandler shopSelectionHandler;
	private @Getter List<String> bypassPlayers = new ArrayList<String>();
	
	private static Economy economy;
	
	private final Logger transactionLogger = Logger.getLogger("transactions");

	public void onEnable() {
		logHandler = new LogHandler(this);
		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler(this);
		resourceHandler = new ResourceHandler(this, this.getClassLoader());
		
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
		saveDefaultConfig();
		reloadConfig();
		
		ConfigurationSerialization.registerClass(ShopData.class);
		ConfigurationSerialization.registerClass(ShopItem.class);
		ConfigurationSerialization.registerClass(SimpleVector.class);
		
		// Register legacy serializables
		ConfigurationSerialization.registerClass(net.t7seven7t.swornmarket.io.legacy.Enchantments.class);
		ConfigurationSerialization.registerClass(net.t7seven7t.swornmarket.io.legacy.ShopItem.class);
		ConfigurationSerialization.registerClass(net.t7seven7t.swornmarket.io.legacy.SimpleVector.class);
		
		if (getConfig().getBoolean("convert-old-data")) {
			ShopDataConverter.run(this);
			getConfig().set("convert-old-data", false);
			saveConfig();
		}
		
		shopDataCache = new ShopDataCache(this);
		shopSelectionHandler = new ShopSelectionHandler();
		
		setupTransactionLogger();
		setupEconomy();
		
		// Register listeners...
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		// Register commands...
		getCommand("shop").setExecutor(commandHandler);
		commandHandler.setCommandPrefix("shop");
		commandHandler.registerCommand(new CmdAdd(this));
		commandHandler.registerCommand(new CmdAddAll(this));
		commandHandler.registerCommand(new CmdBuy(this));
		commandHandler.registerCommand(new CmdBypass(this));
		commandHandler.registerCommand(new CmdCreate(this));
		commandHandler.registerCommand(new CmdDelete(this));
		commandHandler.registerCommand(new CmdHelp(this));
		commandHandler.registerCommand(new CmdInfinite(this));
		commandHandler.registerCommand(new CmdList(this));
		commandHandler.registerCommand(new CmdManager(this));
		commandHandler.registerCommand(new CmdPage(this));
		commandHandler.registerCommand(new CmdRemove(this));
		commandHandler.registerCommand(new CmdRotate(this));
		commandHandler.registerCommand(new CmdSell(this));
		commandHandler.registerCommand(new CmdSet(this));
		commandHandler.registerCommand(new CmdSetName(this));
		commandHandler.registerCommand(new CmdSetOwner(this));
		commandHandler.registerCommand(new CmdTeleport(this));
		commandHandler.registerCommand(new CmdWithdraw(this));

		// Deploy automatic save task
		new BukkitRunnable() {
			
			public void run() {
				shopDataCache.save();
			}
			
		}.runTaskTimerAsynchronously(this, 12000L, Math.round(20L * 60 * getConfig().getDouble("minute-delay-between-saves")));
		
		logHandler.log("Enabled Version {1}", getDescription().getName(), getDescription().getVersion());
	}
	
	public void onDisable() {
		shopDataCache.save();
		
		getServer().getScheduler().cancelTasks(this);
		
		logHandler.log("Disabled Version {1}", getDescription().getName(), getDescription().getVersion());
	}
	
	public String getMessage(String string) {
		try {
			return resourceHandler.getMessages().getString(string);
		} catch (MissingResourceException ex) {
			logHandler.log(Level.WARNING, "Messages locale is missing key for: {0}", string);
			return null;
		}
	}
	
	private void setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
	}
	
	private void setupTransactionLogger() {
		try {
			Handler fileHandler = new FileHandler(getDataFolder() + File.separator + "transactions.log", true);
			fileHandler.setFormatter(new TransactionLogFormatter());
			for (Handler h : transactionLogger.getHandlers())
				transactionLogger.removeHandler(h);
			transactionLogger.setUseParentHandlers(false);
			transactionLogger.addHandler(fileHandler);
			transactionLogger.setLevel(Level.INFO);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	public void logTransaction(Level level, String message) {
		transactionLogger.log(level, message);
	}
	
	public static Economy getEconomy() {
		return economy;
	}
	
}
