package io.basc.framework.util.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class JdkLoggerFactory implements LoggerFactory {

	@Override
	public io.basc.framework.util.logging.Logger getLogger(String name) {
		Logger jdkLogger = Logger.getLogger(name);
		if (jdkLogger.getHandlers().length == 0) {
			jdkLogger.addHandler(new ConsoleHandler());
			jdkLogger.setUseParentHandlers(false);
		}
		return new JdkLogger(jdkLogger);
	}
}
