package io.basc.framework.log4j;

import io.basc.framework.util.logging.ILoggerFactory;
import io.basc.framework.util.logging.Logger;

public class Log4jLoggerFactory implements ILoggerFactory {

	public Logger getLogger(String name) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);
		return new Log4jLogger(logger, null);
	}

}
