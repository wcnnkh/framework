package io.basc.framework.log4j2;

import java.util.function.Supplier;
import java.util.logging.Level;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.CustomLogger;
import io.basc.framework.util.PlaceholderFormat;
import io.basc.framework.util.XUtils;

public class Log4j2Logger extends CustomLogger {
	private final Logger logger;
	private final String placeholder;

	public Log4j2Logger(Logger logger, @Nullable String placeholder) {
		this.placeholder = placeholder;
		this.logger = logger;
		registerListener();
	}

	@Override
	public void setLevel(Level level) {
		if (level != null) {
			Configurator.setLevel(this.logger, LevelCodec.INSTANCE.encode(level));
		}
		super.setLevel(level);
	}

	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isLoggable(Level level) {
		return logger.isEnabled(LevelCodec.INSTANCE.encode(level));
	}

	public void log(Level level, Throwable e, String format, Object... args) {
		org.apache.logging.log4j.Level lv = LevelCodec.INSTANCE.encode(level);
		if (logger.isEnabled(lv)) {
			logger.log(lv, new PlaceholderFormat(format, placeholder, args), e);
		}
	}

	@Override
	public void log(Level level, Throwable e, Supplier<String> msg, Object... args) {
		org.apache.logging.log4j.Level lv = LevelCodec.INSTANCE.encode(level);
		if (logger.isEnabled(lv)) {
			logger.log(lv, XUtils.toString(() -> new PlaceholderFormat(msg.get(), placeholder, args).toString()), e);
		}
	}
}
