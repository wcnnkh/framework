package scw.logger;

import scw.core.instance.InstanceUtils;
import scw.util.FormatUtils;

public final class LoggerFactory {
	public static final ConsoleLoggerFactory CONSOLE_LOGGER_FACTORY = new ConsoleLoggerFactory();
	private static final ILoggerFactory LOGGER_FACTORY;
	
	static{
		ILoggerFactory loggerFactory = InstanceUtils.serviceLoader(ILoggerFactory.class, "scw.logger.log4j2.Log4j2LoggerFactory", "scw.logger.log4j.Log4jLoggerFactory");
		LOGGER_FACTORY = loggerFactory == null? CONSOLE_LOGGER_FACTORY : loggerFactory;
		FormatUtils.info(LoggerFactory.class, "using logger factory [{}]", LOGGER_FACTORY.getClass().getName());
	}
	
	private LoggerFactory() {
	};

	public static Logger getLogger(String name) {
		return getILoggerFactory().getLogger(name);
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name, String placeholder) {
		return getILoggerFactory().getLogger(name, placeholder);
	}

	public static Logger getLogger(Class<?> clazz, String placeholder) {
		return getILoggerFactory().getLogger(clazz.getName(), placeholder);
	}

	/**
	 * 建议使用LoggerUtils.destroy() 此方法调用会初始化logger
	 */
	public static void destroy() {
		getILoggerFactory().destroy();
	}

	public static ILoggerFactory getILoggerFactory() {
		return LOGGER_FACTORY;
	}
}
