package scw.logger.log4j2;

import org.apache.logging.log4j.Logger;

import scw.logger.AbstractLogger;
import scw.logger.Level;
import scw.logger.LoggerLevelManager.DynamicLevel;
import scw.util.PlaceholderFormatAppend;

public class Log4j2Logger extends AbstractLogger {
	private final Logger logger;

	public Log4j2Logger(Logger logger, DynamicLevel level, String placeholder) {
		super(level, placeholder);
		this.logger = logger;
	}

	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return super.isTraceEnabled() || logger.isTraceEnabled();
	}

	@Override
	public void trace(Throwable e, Object msg, Object... args) {
		logger.trace(new PlaceholderFormatAppend(msg, placeholder, args), e);
	}

	@Override
	public boolean isDebugEnabled() {
		return super.isDebugEnabled() || logger.isDebugEnabled();
	}

	@Override
	public void debug(Throwable e, Object msg, Object... args) {
		logger.debug(new PlaceholderFormatAppend(msg, placeholder, args), e);
	}

	@Override
	public boolean isInfoEnabled() {
		return super.isInfoEnabled() || logger.isInfoEnabled();
	}

	@Override
	public void info(Throwable e, Object msg, Object... args) {
		logger.info(new PlaceholderFormatAppend(msg, placeholder, args), e);
	}

	@Override
	public boolean isWarnEnabled() {
		return super.isWarnEnabled() || logger.isWarnEnabled();
	}

	@Override
	public void warn(Throwable e, Object msg, Object... args) {
		logger.warn(new PlaceholderFormatAppend(msg, placeholder, args), e);
	}

	@Override
	public boolean isErrorEnabled() {
		return super.isErrorEnabled() || logger.isErrorEnabled();
	}

	@Override
	public void error(Throwable e, Object msg, Object... args) {
		logger.error(new PlaceholderFormatAppend(msg, placeholder, args), e);
	}

	private static org.apache.logging.log4j.Level parse(Level level) {
		org.apache.logging.log4j.Level lv = org.apache.logging.log4j.Level.getLevel(level.getName());
		if (lv == null) {
			lv = org.apache.logging.log4j.Level.forName(level.getName(), level.getValue());
		}
		return lv;
	}

	@Override
	public boolean isLogEnable(Level level) {
		return super.isLogEnable(level) || logger.isEnabled(parse(level));
	}

	public void log(Level level, Throwable e, Object format, Object... args) {
		org.apache.logging.log4j.Level lv = parse(level);
		if (super.isLogEnable(level) || logger.isEnabled(lv)) {
			logger.log(lv, new PlaceholderFormatAppend(format, placeholder, args), e);
		}
	}
}
