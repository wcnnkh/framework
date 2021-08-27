package io.basc.framework.log4j2;

import io.basc.framework.logger.ILoggerFactory;
import io.basc.framework.logger.Logger;

import org.apache.logging.log4j.LogManager;

public class Log4j2LoggerFactory implements ILoggerFactory {

	static {
		LogManager.class.getName();
	}

	public void destroy() {
		LogManager.shutdown();
	}

	public Logger getLogger(String name) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		return new Log4j2Logger(logger, null);
	}
}