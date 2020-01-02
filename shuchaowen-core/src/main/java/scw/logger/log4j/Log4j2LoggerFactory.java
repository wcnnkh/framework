package scw.logger.log4j;

import org.apache.logging.log4j.LogManager;

import scw.lang.NotSupportException;
import scw.logger.AbstractILoggerFactory;
import scw.logger.Logger;
import scw.logger.LoggerLevelUtils;

public class Log4j2LoggerFactory extends AbstractILoggerFactory {

	static {
		try {
			Class.forName("org.apache.logging.log4j.LogManager");
		} catch (ClassNotFoundException e) {
			throw new NotSupportException("log4j2");
		}
	}

	public void destroy() {
		LogManager.shutdown();
	}

	public Logger getLogger(String name, String placeholder) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		return new Log4j2Logger(logger, LoggerLevelUtils.getLevel(name), placeholder);
	}

}
