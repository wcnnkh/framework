package scw.logger.slf4j;

import org.slf4j.LoggerFactory;

import scw.logger.AsyncLogger;
import scw.logger.AsyncLoggerFactory;
import scw.logger.Level;
import scw.logger.Logger;

/**
 * 由slf4j去整合其他框架
 * @author shuchaowen
 *
 */
public class Slf4jILoggerFactory extends AsyncLoggerFactory {
	public Slf4jILoggerFactory() {
		super("scw-slf4j");
	}

	public Logger getLogger(String name) {
		org.slf4j.Logger logger = LoggerFactory.getLogger(name);
		return new AsyncLogger(logger.isTraceEnabled(), logger.isDebugEnabled(), logger.isInfoEnabled(),
				logger.isWarnEnabled(), logger.isErrorEnabled(), logger.getName(), this);
	}

	@Override
	public void out(String name, Level level, String msg, Throwable e) throws Exception {
		org.slf4j.Logger logger = LoggerFactory.getLogger(name);
		switch (level) {
		case INFO:
			logger.info(msg, e);
			break;
		case DEBUG:
			logger.debug(msg, e);
		case ERROR:
			logger.error(msg, e);
		case TRACE:
			logger.trace(msg, e);
		case WARN:
			logger.warn(msg, e);
		default:
			break;
		}
	}
}
