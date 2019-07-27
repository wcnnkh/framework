package scw.logger.log4j;

import org.apache.log4j.LogManager;

import scw.core.UnsafeStringBuffer;
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
		return Log4jUtils.getLogger(name, this);
	}

	@Override
	public void out(UnsafeStringBuffer unsafeStringBuffer, Message message)
			throws Exception {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger
				.getLogger(message.getName());
		switch (message.getLevel()) {
		case DEBUG:
			logger.debug(message.toMessage(unsafeStringBuffer),
					message.getThrowable());
			break;
		case ERROR:
			logger.error(message.toMessage(unsafeStringBuffer),
					message.getThrowable());
		case INFO:
			logger.info(message.toMessage(unsafeStringBuffer),
					message.getThrowable());
		case TRACE:
			logger.trace(message.toMessage(unsafeStringBuffer),
					message.getThrowable());
		case WARN:
			logger.warn(message.toMessage(unsafeStringBuffer),
					message.getThrowable());
		default:
			break;
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		LogManager.shutdown();
	}
}
