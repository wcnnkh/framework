package scw.logger.log4j2;

import org.apache.logging.log4j.LogManager;

import scw.core.instance.annotation.Configuration;
import scw.lang.UnsupportedException;
import scw.logger.AbstractILoggerFactory;
import scw.logger.Logger;
import scw.logger.LoggerLevelUtils;

@Configuration(order=Integer.MIN_VALUE + 100)
public class Log4j2LoggerFactory extends AbstractILoggerFactory {

	static {
		try {
			Class.forName("org.apache.logging.log4j.LogManager");
		} catch (ClassNotFoundException e) {
			throw new UnsupportedException("log4j2");
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
