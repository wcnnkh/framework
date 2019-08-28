package scw.logger.log4j;

import org.apache.logging.log4j.Logger;

import scw.logger.AbstractLogger;

public class Log4j2Logger extends AbstractLogger {
	private final Logger logger;

	public Log4j2Logger(Logger logger, String placeholder) {
		super(placeholder);
		this.logger = logger;
	}

	public Logger getLogger() {
		return logger;
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

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	public void warn(Object format, Object... args) {
		logger.warn(createMessage(format, args));
	}

	public void warn(Throwable e, Object format, Object... args) {
		logger.warn(createMessage(format, args), e);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
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
