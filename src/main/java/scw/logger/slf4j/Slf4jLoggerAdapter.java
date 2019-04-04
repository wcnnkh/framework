package scw.logger.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLoggerAdapter implements scw.logger.Logger {
	private final Logger logger;

	public Slf4jLoggerAdapter(Class<?> clz) {
		logger = LoggerFactory.getLogger(clz);
	}

	public Slf4jLoggerAdapter(String name) {
		logger = LoggerFactory.getLogger(name);
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public void trace(String msg) {
		logger.trace(msg);
	}

	public void trace(String format, Object... args) {
		logger.trace(format, args);
	}

	public void trace(String msg, Throwable t) {
		logger.trace(msg, t);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public void debug(String msg) {
		logger.debug(msg);
	}

	public void debug(String format, Object... args) {
		logger.debug(format, args);
	}

	public void debug(String msg, Throwable t) {
		logger.debug(msg, t);
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public void info(String msg) {
		logger.info(msg);
	}

	public void info(String format, Object... args) {
		logger.info(format, args);
	}

	public void info(String msg, Throwable t) {
		logger.info(msg, t);
	}

	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	public void warn(String msg) {
		logger.warn(msg);
	}

	public void warn(String format, Object... args) {
		logger.warn(format, args);
	}

	public void warn(String msg, Throwable t) {
		logger.warn(msg, t);
	}

	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	public void error(String msg) {
		logger.error(msg);
	}

	public void error(String format, Object... args) {
		logger.error(format, args);
	}

	public void error(String msg, Throwable t) {
		logger.error(msg, t);
	}
}
