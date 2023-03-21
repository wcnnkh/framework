package io.basc.framework.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class JdkLoggerFactory implements ILoggerFactory {
	private static final Logger ROOT_LOGGER = Logger.getLogger(JdkLoggerFactory.class.getName());

	static {
		ROOT_LOGGER.addHandler(new ConsoleHandler());
		ROOT_LOGGER.setUseParentHandlers(false);
	}

	public static Logger getRootLogger() {
		return ROOT_LOGGER;
	}

	@Override
	public io.basc.framework.logger.Logger getLogger(String name) {
		Logger jdkLogger = Logger.getLogger(name);
		if (jdkLogger != ROOT_LOGGER) {
			jdkLogger.setParent(ROOT_LOGGER);
		}
		return new JdkLogger(jdkLogger);
	}
}
