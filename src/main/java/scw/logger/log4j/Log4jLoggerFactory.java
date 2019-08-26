package scw.logger.log4j;

import org.apache.log4j.LogManager;

import scw.core.UnsafeStringBuffer;
import scw.logger.AsyncLogger;
import scw.logger.AsyncLoggerFactory;
import scw.logger.Logger;
import scw.logger.Message;

public class Log4jLoggerFactory extends AsyncLoggerFactory {

	public Log4jLoggerFactory() {
		super("scw-log4j");
		Log4jUtils.defaultInit();
	}

	@Override
	public Logger getLogger(String name) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);
		return new AsyncLogger(logger.isTraceEnabled(), logger.isDebugEnabled(), logger.isInfoEnabled(), true, true,
				name, this);
	}

	@Override
	public void out(UnsafeStringBuffer unsafeStringBuffer, Message message) throws Exception {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(message.getName());
		if (message.getThrowable() == null) {
			switch (message.getLevel()) {
			case DEBUG:
				logger.debug(message.toMessage(unsafeStringBuffer));
				break;
			case ERROR:
				logger.error(message.toMessage(unsafeStringBuffer));
			case INFO:
				logger.info(message.toMessage(unsafeStringBuffer));
			case TRACE:
				logger.trace(message.toMessage(unsafeStringBuffer));
			case WARN:
				logger.warn(message.toMessage(unsafeStringBuffer));
			default:
				break;
			}
		} else {
			switch (message.getLevel()) {
			case DEBUG:
				logger.debug(message.toMessage(unsafeStringBuffer), message.getThrowable());
				break;
			case ERROR:
				logger.error(message.toMessage(unsafeStringBuffer), message.getThrowable());
			case INFO:
				logger.info(message.toMessage(unsafeStringBuffer), message.getThrowable());
			case TRACE:
				logger.trace(message.toMessage(unsafeStringBuffer), message.getThrowable());
			case WARN:
				logger.warn(message.toMessage(unsafeStringBuffer), message.getThrowable());
			default:
				break;
			}
		}

	}

	@Override
	public void destroy() {
		super.destroy();
		LogManager.shutdown();
	}
}
