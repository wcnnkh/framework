package scw.logger.log4j;

import org.apache.log4j.LogManager;

import scw.logger.ILoggerFactory;
import scw.logger.Logger;

public class Log4jLoggerFactory implements ILoggerFactory {

	public Log4jLoggerFactory() {
		Log4jUtils.defaultInit();
	}

	public void destroy() {
		LogManager.shutdown();
	}

	public Logger getLogger(String name) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);
		return new Log4jLogger(logger, null);
	}
}
