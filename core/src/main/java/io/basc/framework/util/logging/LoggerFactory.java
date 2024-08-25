package io.basc.framework.util.logging;

public final class LoggerFactory {
	private static final DynamicLoggerFactory SOURCE = new DynamicLoggerFactory();

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name) {
		if (!SOURCE.isConfigured()) {
			SOURCE.configure();
		}
		return SOURCE.getLogger(name);
	}

	public static DynamicLoggerFactory getSource() {
		return SOURCE;
	}

	private LoggerFactory() {
		throw new UnsupportedOperationException();
	};
}
