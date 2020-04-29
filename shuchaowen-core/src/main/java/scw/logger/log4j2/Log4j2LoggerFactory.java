package scw.logger.log4j2;

import org.apache.logging.log4j.LogManager;

import scw.core.instance.annotation.Configuration;
import scw.logger.AbstractILoggerFactory;
import scw.logger.Logger;
import scw.logger.LoggerLevelUtils;

@Configuration(order=Integer.MIN_VALUE + 400)
public class Log4j2LoggerFactory extends AbstractILoggerFactory {

	static {
		LogManager.class.getName();
	}

	public void destroy() {
		LogManager.shutdown();
	}

	public Logger getLogger(String name, String placeholder) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		return new Log4j2Logger(logger, LoggerLevelUtils.getLevel(name), placeholder);
	}
}
