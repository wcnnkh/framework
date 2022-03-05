package io.basc.framework.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import io.basc.framework.logger.ILoggerFactory;
import io.basc.framework.logger.Logger;

public class Log4j2LoggerFactory implements ILoggerFactory {

	static {
		LogManager.class.getName();
		Configurator.initialize(null, "io/basc/framework/log4j2/configuration.xml");
	}

	public void destroy() {
		LogManager.shutdown();
	}

	public Logger getLogger(String name) {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(name);
		return new Log4j2Logger(logger, null);
	}
}