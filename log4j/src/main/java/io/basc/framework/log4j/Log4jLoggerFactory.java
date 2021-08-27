package io.basc.framework.log4j;

import io.basc.framework.logger.ILoggerFactory;
import io.basc.framework.logger.Logger;

public class Log4jLoggerFactory implements ILoggerFactory {

	public Logger getLogger(String name) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);
		return new Log4jLogger(logger, null);
	}
	
}
