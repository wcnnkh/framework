package io.basc.framework.log4j2;

import org.apache.logging.log4j.LogManager;

import io.basc.framework.logger.ILoggerFactory;
import io.basc.framework.logger.Logger;

public class Log4j2LoggerFactory implements ILoggerFactory {

	static {
		Log4j2Utils.defaultInit();
	}

	public void destroy() {
		LogManager.shutdown();
	}

	public Logger getLogger(String name) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		return new Log4j2Logger(logger, null);
	}
}