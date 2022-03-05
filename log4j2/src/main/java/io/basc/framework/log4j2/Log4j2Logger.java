package io.basc.framework.log4j2;

import java.util.logging.Level;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.CustomLogger;
import io.basc.framework.util.PlaceholderFormat;

public class Log4j2Logger extends CustomLogger {
	private final Logger logger;
	private final String placeholder;

	public Log4j2Logger(Logger logger, @Nullable String placeholder) {
		this.placeholder = placeholder;
		this.logger = logger;
	}
	
	@Override
	public void setLevel(Level level) {
		Configurator.setLevel(this.logger, parse(level));
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
	public void trace(Throwable e, String msg, Object... args) {
		logger.trace(new PlaceholderFormat(msg, placeholder, args), e);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(Throwable e, String msg, Object... args) {
		logger.debug(new PlaceholderFormat(msg, placeholder, args), e);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(Throwable e, String msg, Object... args) {
		logger.info(new PlaceholderFormat(msg, placeholder, args), e);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(Throwable e, String msg, Object... args) {
		logger.warn(new PlaceholderFormat(msg, placeholder, args), e);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(Throwable e, String msg, Object... args) {
		logger.error(new PlaceholderFormat(msg, placeholder, args), e);
	}

	private static org.apache.logging.log4j.Level parse(Level level) {
		org.apache.logging.log4j.Level lv = org.apache.logging.log4j.Level.getLevel(level.getName());
		if (lv == null) {
			lv = org.apache.logging.log4j.Level.forName(level.getName(), level.intValue());
		}
		return lv;
	}

	@Override
	public boolean isLoggable(Level level) {
		return logger.isEnabled(parse(level));
	}

	public void log(Level level, Throwable e, String format, Object... args) {
		org.apache.logging.log4j.Level lv = parse(level);
		if (logger.isEnabled(lv)) {
			logger.log(lv, new PlaceholderFormat(format, placeholder, args), e);
		}
	}
}
