package scw.logger;

import scw.logger.console.ConsoleLoggerFactory;

public final class LoggerFactory {
	private static ILoggerFactory CONSOLE_LOGGER_FACTORY = new ConsoleLoggerFactory();

	private LoggerFactory() {
	};

	public static Logger getLogger(String name) {
		return getILoggerFactory().getLogger(name);
	}

	public static Logger getLogger(Class<?> clz) {
		return getLogger(clz.getName());
	}

	public static void destroy() {
		getILoggerFactory().destroy();
	}

	public static ILoggerFactory getILoggerFactory() {
		return CONSOLE_LOGGER_FACTORY;
	}
}
