package scw.logger;

import scw.core.instance.InstanceUtils;

public final class LoggerFactory {
	private static final ILoggerFactory LOGGER_FACTORY;

	private LoggerFactory() {
	};

	static {
		String[] supportArr = { "scw.logger.log4j.Log4jLoggerFactory" };

		ILoggerFactory loggerFactory = null;
		for (String name : supportArr) {
			loggerFactory = InstanceUtils.getInstance(name);
		}

		LOGGER_FACTORY = loggerFactory == null ? new ConsoleLoggerFactory()
				: loggerFactory;

		System.out.println("Init shuchaowen-logger ["
				+ getILoggerFactory().getClass().getName() + "]");
	}

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
		return LOGGER_FACTORY;
	}
}
