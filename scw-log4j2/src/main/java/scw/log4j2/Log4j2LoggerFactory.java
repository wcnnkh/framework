package scw.log4j2;

import org.apache.logging.log4j.LogManager;

import scw.logger.ILoggerFactory;
import scw.logger.Logger;
import scw.logger.LoggerLevelManager;

public class Log4j2LoggerFactory implements ILoggerFactory {

	static {
		LogManager.class.getName();
	}

	public void destroy() {
		LogManager.shutdown();
	}

	public Logger getLogger(String name, String placeholder) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		return new Log4j2Logger(logger, LoggerLevelManager.getInstance().getLevel(name), placeholder);
	}
}
