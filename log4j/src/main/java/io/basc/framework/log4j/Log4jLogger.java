package io.basc.framework.log4j;

import java.util.function.Supplier;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.CustomLogger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.PlaceholderFormat;
import io.basc.framework.util.XUtils;

public class Log4jLogger extends CustomLogger {
	private final Logger logger;
	private final String placeholder;

	public Log4jLogger(Logger logger, @Nullable String placeholder) {
		this.placeholder = placeholder;
		this.logger = logger;
		Level level = LoggerFactory.getLevelManager().get().getLevel(logger.getName());
		if (level != null) {
			setLevel(level);
		}
		registerListener();
	}

	public String getName() {
		return logger.getName();
	}

	@Override
	public void setLevel(Level level) {
		logger.setLevel(parse(level));
		super.setLevel(level);
	}

	@Override
	public boolean isTraceEnabled() {
		return super.isTraceEnabled() || logger.isTraceEnabled();
	}

	@Override
	public void trace(Throwable e, String msg, Object... args) {
		logger.trace(new PlaceholderFormat(msg, placeholder, args), e);
	}

	@Override
	public boolean isDebugEnabled() {
		return super.isDebugEnabled() || logger.isDebugEnabled();
	}

	@Override
	public void debug(Throwable e, String msg, Object... args) {
		logger.debug(new PlaceholderFormat(msg, placeholder, args), e);
	}

	@Override
	public boolean isInfoEnabled() {
		return super.isInfoEnabled() || logger.isInfoEnabled();
	}

	@Override
	public void info(Throwable e, String msg, Object... args) {
		logger.info(new PlaceholderFormat(msg, placeholder, args), e);
	}

	@Override
	public boolean isWarnEnabled() {
		return super.isWarnEnabled() || logger.isEnabledFor(org.apache.log4j.Level.WARN);
	}

	@Override
	public void warn(Throwable e, String msg, Object... args) {
		logger.warn(new PlaceholderFormat(msg, placeholder, args), e);
	}

	@Override
	public boolean isErrorEnabled() {
		return super.isErrorEnabled() || logger.isEnabledFor(org.apache.log4j.Level.ERROR);
	}

	@Override
	public void error(Throwable e, String msg, Object... args) {
		logger.error(new PlaceholderFormat(msg, placeholder, args), e);
	}

	private static org.apache.log4j.Level parse(Level level) {
		if (level == null) {
			return org.apache.log4j.Level.INFO;
		}
		return org.apache.log4j.Level.toLevel(level.getName(), new CustomLog4jLevel(level));
	}

	public boolean isLoggable(Level level) {
		return logger.isEnabledFor(parse(level));
	}

	public void log(Level level, Throwable e, String format, Object... args) {
		org.apache.log4j.Level lv = parse(level);
		if (logger.isEnabledFor(lv)) {
			logger.log(lv, new PlaceholderFormat(format, placeholder, args), e);
		}
	}

	@Override
	public void log(Level level, Throwable e, Supplier<String> msg, Object... args) {
		org.apache.log4j.Level lv = parse(level);
		if (logger.isEnabledFor(lv)) {
			logger.log(lv, XUtils.toString(() -> new PlaceholderFormat(msg.get(), placeholder, args).toString()), e);
		}
	}
}
