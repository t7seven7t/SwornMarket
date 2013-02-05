/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornmarket;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author t7seven7t
 */
public class TransactionLogFormatter extends Formatter {

	private final SimpleDateFormat date;
	
	public TransactionLogFormatter() {
		date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(date.format(record.getMillis()));
		builder.append(" [");
		if (record.getLevel().equals(Level.INFO))
			builder.append("SELL");
		else if (record.getLevel().equals(Level.WARNING))
			builder.append("BUY");
		builder.append("] ");
		builder.append(formatMessage(record));
		builder.append('\n');
		
		return builder.toString();
	}
	
}
