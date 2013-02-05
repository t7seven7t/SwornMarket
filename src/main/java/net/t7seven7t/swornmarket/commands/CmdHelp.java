/**
Copyright (C) 2012 t7seven7t
**/
package net.t7seven7t.swornmarket.commands;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornmarket.SwornMarket;
import net.t7seven7t.util.FormatUtil;

/**
 * @author t7seven7t
 * Help command that shows descriptions of other commands. 
 * Has pagination.
 */
public class CmdHelp extends PaginatedCommand {

	public CmdHelp(SwornMarket plugin) {
		super(plugin);
		this.name = "help";
		this.description = "Shows " + plugin.getName() + " help.";
		this.optionalArgs.add("page");
		this.linesPerPage = 6;
	}

	@Override
	public int getListSize() {
		return plugin.getCommandHandler().getRegisteredCommands().size();
	}

	@Override
	public String getHeader(int index) {
		return FormatUtil.format("&2{0} Help (&ePage {1}/{2}&2):", plugin.getName(), index, getPageCount());
	}

	@Override
	public List<String> getLines(int startIndex, int endIndex) {
		List<String> lines = new ArrayList<String>();
		for (int i = startIndex; i < endIndex && i < getListSize(); i++) {
			SwornMarketCommand command;
			command = plugin.getCommandHandler().getRegisteredCommands().get(i);
			
			if (plugin.getPermissionHandler().hasPermission(sender, permission))
				lines.add(command.getUsageTemplate(true));
		}
		return lines;
	}

	
	@Override
	public String getLine(int index) {
		// Unnecessary since we override getLines();
		return null;
	}


}
