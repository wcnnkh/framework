package io.basc.framework.log4j;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.log4j.Logger;

import io.basc.framework.util.logging.AbstractLogger;
import io.basc.framework.util.placeholder.FormatableMessage;

public class Log4jLogger extends AbstractLogger {
	private final Logger logger;
	private final String placeholder;

	public Log4jLogger(Logger logger, String placeholder) {
		this.placeholder = placeholder;
		this.logger = logger;
	}

	public String getName() {
		return logger.getName();
	}

	private static org.apache.log4j.Level parse(Level level) {
		if (level == null) {
			return org.apache.log4j.Level.INFO;
		}
		return org.apache.log4j.Level.toLevel(level.getName(), new CustomLog4jLevel(level));
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		logger.setLevel(parse(level));
	}

	public boolean isLoggable(Level level) {
		return super.isLoggable(level) && logger.isEnabledFor(parse(level));
	}

	@Override
	public void log(LogRecord record) {
		org.apache.log4j.Level lv = parse(record.getLevel());
		logger.log(lv, new FormatableMessage(record.getMessage(), placeholder, record.getParameters()),
				record.getThrown());
	}
}
