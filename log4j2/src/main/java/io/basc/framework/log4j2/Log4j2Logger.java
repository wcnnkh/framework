package io.basc.framework.log4j2;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.placeholder.FormatableMessage;

public class Log4j2Logger implements io.basc.framework.util.logging.Logger {
	private final Logger logger;
	private final String placeholder;

	public Log4j2Logger(Logger logger, @Nullable String placeholder) {
		this.placeholder = placeholder;
		this.logger = logger;
	}

	@Override
	public void setLevel(Level level) {
		Configurator.setLevel(this.logger, LevelCodec.INSTANCE.encode(level));
	}

	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isLoggable(Level level) {
		return logger.isEnabled(LevelCodec.INSTANCE.encode(level));
	}

	@Override
	public void log(LogRecord record) {
		org.apache.logging.log4j.Level lv = LevelCodec.INSTANCE.encode(record.getLevel());
		logger.log(lv, new FormatableMessage(record.getMessage(), placeholder, record.getParameters()),
				record.getThrown());
	}
}
