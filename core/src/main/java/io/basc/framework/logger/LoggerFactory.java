package io.basc.framework.logger;

import io.basc.framework.beans.factory.support.SimpleServiceLoaderFactory;

public final class LoggerFactory {
	private static final DynamicLoggerFactory SOURCE = new DynamicLoggerFactory();

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name) {
		if (!SOURCE.isConfigured()) {
			SOURCE.configure(SimpleServiceLoaderFactory.INSTANCE);
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
