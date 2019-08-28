package scw.logger.log4j;

import org.apache.log4j.Logger;

import scw.logger.AbstractLogger;

public class Log4jLogger extends AbstractLogger {
	private final Logger logger;

	public Log4jLogger(Logger logger, String placeholder) {
		super(placeholder);
		this.logger = logger;
	}

	public String getName() {
		return logger.getName();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public void info(Object format, Object... args) {
		logger.info(createMessage(format, args));
	}

	public void info(Throwable e, Object format, Object... args) {
		logger.info(createMessage(format, args), e);
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public void trace(Object format, Object... args) {
		logger.trace(createMessage(format, args));
	}

	public void trace(Throwable e, Object format, Object... args) {
		logger.trace(createMessage(format, args), e);
	}

	public void warn(Object format, Object... args) {
		logger.warn(createMessage(format, args));
	}

	public void warn(Throwable e, Object format, Object... args) {
		logger.warn(createMessage(format, args), e);
	}

	public void error(Object format, Object... args) {
		logger.error(createMessage(format, args));
	}

	public void error(Throwable e, Object format, Object... args) {
		logger.error(createMessage(format, args), e);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public void debug(Object format, Object... args) {
		logger.debug(createMessage(format, args));
	}

	public void debug(Throwable e, Object format, Object... args) {
		logger.debug(createMessage(format, args), e);
	}

}
