package scw.logger;

import scw.core.reflect.ReflectUtils;

public final class LoggerFactory {
	private static final ILoggerFactory CONSOLE_LOGGER_FACTORY = new ConsoleLoggerFactory();
	@SuppressWarnings("unused")
	private static ILoggerFactory sl4j;

	private LoggerFactory() {
	};

	static {
		try {
			Class<?> clz = Class.forName("scw.logger.slf4j.Sl4jILoggerFactory");
			sl4j = ReflectUtils.newInstance(clz);
		} catch (Throwable e) {
			// ignore
		}

		System.out.println("Init shuchaowen-logger [" + getILoggerFactory().getClass().getName() + "]");
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
		return CONSOLE_LOGGER_FACTORY;
		/*
		 * if(sl4j == null){ return CONSOLE_LOGGER_FACTORY; }else{ return sl4j;
		 * }
		 */
	}
}
