package io.basc.framework.logger;

public final class LoggerFactory {
	private static final DynamicLoggerFactory LOGGER_FACTORY = new DynamicLoggerFactory();
	private static final LevelManager LEVEL_MANAGER = LOGGER_FACTORY.getServiceLoaderFactory()
			.getServiceLoader(LevelManager.class).findFirst().orElseGet(() -> new LevelManager());

	public static LevelManager getLevelManager() {
		return LEVEL_MANAGER;
	}

	public static ILoggerFactory getLoggerFactory() {
		return LOGGER_FACTORY;
	}

	public static Logger getLogger(String name) {
		return LOGGER_FACTORY.getLogger(name);
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	private LoggerFactory() {
		throw new UnsupportedOperationException();
	};
}
