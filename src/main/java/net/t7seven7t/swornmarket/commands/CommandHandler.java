/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket.commands;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornmarket.SwornMarket;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author t7seven7t
 */
public class CommandHandler implements CommandExecutor {
	private final SwornMarket plugin;

	private List<SwornMarketCommand> registeredCommands;
	
	public CommandHandler(SwornMarket plugin) {
		this.plugin = plugin;
		registeredCommands = new ArrayList<SwornMarketCommand>();
	}
	
	public void registerCommand(SwornMarketCommand command) {
		registeredCommands.add(command);
	}

	public List<SwornMarketCommand> getRegisteredCommands() {
		return registeredCommands;
	}

	public void setCommandPrefix(String commandPrefix) {
		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> argsList = new ArrayList<String>();
		
		if (args.length > 0) {
			String commandName = args[0];
			for (int i = 1; i < args.length; i++)
				argsList.add(args[i]);
			
			for (SwornMarketCommand command : registeredCommands) {
				if (commandName.equalsIgnoreCase(command.getName()) || command.getAliases().contains(commandName.toLowerCase()))
					command.execute(sender, argsList.toArray(new String[0]));
			}
		} else {
			new CmdHelp(plugin).execute(sender, args);
		}
		
		return true;
	}
	
}
