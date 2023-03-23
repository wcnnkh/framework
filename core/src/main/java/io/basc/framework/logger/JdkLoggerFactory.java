package io.basc.framework.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class JdkLoggerFactory implements ILoggerFactory {

	@Override
	public io.basc.framework.logger.Logger getLogger(String name) {
		Logger jdkLogger = Logger.getLogger(name);
		if (jdkLogger.getHandlers().length == 0) {
			jdkLogger.addHandler(new ConsoleHandler());
			jdkLogger.setUseParentHandlers(false);
		}
		return new JdkLogger(jdkLogger);
	}
}
