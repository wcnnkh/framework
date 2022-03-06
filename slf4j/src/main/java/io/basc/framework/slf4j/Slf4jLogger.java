package io.basc.framework.slf4j;

import io.basc.framework.logger.CustomLevel;
import io.basc.framework.logger.CustomLogger;
import io.basc.framework.util.PlaceholderFormat;

import java.util.function.Supplier;
import java.util.logging.Level;

import org.slf4j.Logger;

/**
 * 并非支持所有的日志等级, 仅支持常规的info, debug, trace, warn, error
 * 
 * @author shuchaowen
 *
 */
public class Slf4jLogger extends CustomLogger {
	private static final String FORMAT = "{}";
	private final Logger logger;
	private final String placeholder;

	public Slf4jLogger(Logger logger, String placeholder) {
		this.logger = logger;
		this.placeholder = placeholder;
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isLoggable(Level level) {
		if (level.getName().equalsIgnoreCase(Level.INFO.getName())) {
			return logger.isInfoEnabled();
		} else if (level.getName().equalsIgnoreCase(CustomLevel.DEBUG.getName())) {
			return logger.isDebugEnabled();
		} else if (level.getName().equalsIgnoreCase(CustomLevel.TRACE.getName())) {
			return logger.isTraceEnabled();
		} else if (level.getName().equalsIgnoreCase(CustomLevel.WARN.getName())) {
			return logger.isWarnEnabled();
		} else if (level.getName().equalsIgnoreCase(CustomLevel.ERROR.getName())) {
			return logger.isErrorEnabled();
		} else {
			return super.isLoggable(level);
		}
	}

	@Override
	public void log(Level level, Throwable e, String msg, Object... args) {
		PlaceholderFormat message = new PlaceholderFormat(msg, placeholder, args);
		if (level.getName().equalsIgnoreCase(Level.INFO.getName())) {
			if (e == null) {
				logger.info(FORMAT, message);
			} else {
				logger.info(message.toString(), e);
			}
		} else if (level.getName().equalsIgnoreCase(CustomLevel.DEBUG.getName())) {
			if (e == null) {
				logger.debug(FORMAT, message);
			} else {
				logger.debug(message.toString(), e);
			}
		} else if (level.getName().equalsIgnoreCase(CustomLevel.TRACE.getName())) {
			if (e == null) {
				logger.trace(FORMAT, message);
			} else {
				logger.trace(message.toString(), e);
			}
		} else if (level.getName().equalsIgnoreCase(CustomLevel.WARN.getName())) {
			if (e == null) {
				logger.warn(FORMAT, message);
			} else {
				logger.warn(message.toString(), e);
			}
		} else if (level.getName().equalsIgnoreCase(CustomLevel.ERROR.getName())) {
			if (e == null) {
				logger.error(FORMAT, message);
			} else {
				logger.error(message.toString(), e);
			}
		} else {
			if (e == null) {
				logger.info("Unsupported {} | {}", level, message);
			} else {
				logger.info("Unsupported " + level + " | " + message, e);
			}
		}
	}

	@Override
	public void log(Level level, Throwable e, Supplier<String> msg, Object... args) {
		log(level, e, msg.get(), args);
	}

}
