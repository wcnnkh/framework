package io.basc.framework.util.logging;

public final class LogManager {
	private static final JdkLoggerFactory JDK_LOGGER_FACTORY = new JdkLoggerFactory();
	private static DynamicLoggerFactory source = new DynamicLoggerFactory(JDK_LOGGER_FACTORY);

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name) {
		return source.getLogger(name);
	}

	public static DynamicLoggerFactory getSource() {
		return source;
	}

	private LogManager() {
		throw new UnsupportedOperationException();
	};
}
