package io.basc.framework.logger;

public class JdkLoggerFactory implements ILoggerFactory {
	private final java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger(getClass().getName());

	@Override
	public Logger getLogger(String name) {
		java.util.logging.Logger jdkLogger = java.util.logging.Logger.getLogger(name);
		if (jdkLogger != rootLogger) {
			jdkLogger.setParent(rootLogger);
		}
		return new JdkLogger(jdkLogger);
	}

	public java.util.logging.Logger getRootLogger() {
		return rootLogger;
	}
}
