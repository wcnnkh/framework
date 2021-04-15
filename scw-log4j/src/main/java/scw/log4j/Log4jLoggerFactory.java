package scw.log4j;

import scw.logger.ILoggerFactory;
import scw.logger.Logger;

public class Log4jLoggerFactory implements ILoggerFactory {

	static {
		org.apache.log4j.Logger.class.getName();
		Log4jUtils.defaultInit();
	}

	public Logger getLogger(String name) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);
		return new Log4jLogger(logger, null);
	}
}
