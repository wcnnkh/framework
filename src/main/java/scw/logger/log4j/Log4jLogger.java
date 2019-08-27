package scw.logger.log4j;

import org.apache.log4j.Logger;

import scw.core.utils.StringAppend;
import scw.logger.AbstractLogger;
import scw.logger.DefaultLoggerFormatAppend;

public class Log4jLogger extends AbstractLogger {
	private final Logger logger;

	public Log4jLogger(Logger logger, String placeholder) {
		super(placeholder);
		this.logger = logger;
	}

	protected StringAppend createStringAppend(Object format, Object... args) {
		return new DefaultLoggerFormatAppend(format, getPlaceholder(), args);
	}

	public String getName() {
		return logger.getName();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public void info(Object format, Object... args) {
		logger.info(createStringAppend(format, args));
	}

	public void info(Throwable e, Object format, Object... args) {
		logger.info(createStringAppend(format, args), e);
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public void trace(Object format, Object... args) {
		logger.trace(createStringAppend(format, args));
	}

	public void trace(Throwable e, Object format, Object... args) {
		logger.trace(createStringAppend(format, args), e);
	}

	public void warn(Object format, Object... args) {
		logger.warn(createStringAppend(format, args));
	}

	public void warn(Throwable e, Object format, Object... args) {
		logger.warn(createStringAppend(format, args), e);
	}

	public void error(Object format, Object... args) {
		logger.error(createStringAppend(format, args));
	}

	public void error(Throwable e, Object format, Object... args) {
		logger.error(createStringAppend(format, args), e);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public void debug(Object format, Object... args) {
		logger.debug(createStringAppend(format, args));
	}

	public void debug(Throwable e, Object format, Object... args) {
		logger.debug(createStringAppend(format, args), e);
	}

}
