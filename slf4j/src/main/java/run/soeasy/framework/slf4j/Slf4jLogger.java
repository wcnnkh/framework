package run.soeasy.framework.slf4j;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.slf4j.Logger;

import run.soeasy.framework.logging.CustomLevel;
import run.soeasy.framework.logging.FormatableMessage;

/**
 * 并非支持所有的日志等级, 仅支持常规的info, debug, trace, warn, error
 * 
 * @author wcnnkh
 *
 */
public class Slf4jLogger implements run.soeasy.framework.logging.Logger {
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
			return true;
		}
	}

	@Override
	public void log(LogRecord record) {
		FormatableMessage message = new FormatableMessage(record.getMessage(), placeholder, record.getParameters());
		Throwable e = record.getThrown();
		if (record.getLevel().getName().equalsIgnoreCase(Level.INFO.getName())) {
			if (e == null) {
				logger.info(FORMAT, message);
			} else {
				logger.info(message.toString(), e);
			}
		} else if (record.getLevel().getName().equalsIgnoreCase(CustomLevel.DEBUG.getName())) {
			if (e == null) {
				logger.debug(FORMAT, message);
			} else {
				logger.debug(message.toString(), e);
			}
		} else if (record.getLevel().getName().equalsIgnoreCase(CustomLevel.TRACE.getName())) {
			if (e == null) {
				logger.trace(FORMAT, message);
			} else {
				logger.trace(message.toString(), e);
			}
		} else if (record.getLevel().getName().equalsIgnoreCase(CustomLevel.WARN.getName())) {
			if (e == null) {
				logger.warn(FORMAT, message);
			} else {
				logger.warn(message.toString(), e);
			}
		} else if (record.getLevel().getName().equalsIgnoreCase(CustomLevel.ERROR.getName())) {
			if (e == null) {
				logger.error(FORMAT, message);
			} else {
				logger.error(message.toString(), e);
			}
		} else {
			if (e == null) {
				logger.info("Unsupported {} | {}", record.getLevel(), message);
			} else {
				logger.info("Unsupported " + record.getLevel() + " | " + message, e);
			}
		}
	}
}
