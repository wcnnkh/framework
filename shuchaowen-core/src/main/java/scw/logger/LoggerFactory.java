package scw.logger;

import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.util.FormatUtils;

public final class LoggerFactory {
	private static final ILoggerFactory LOGGER_FACTORY;

	private LoggerFactory() {
	};

	private static String[] getSupperLoggerFactory() {
		String value = SystemPropertyUtils.getProperty("scw.logger.factory");
		return StringUtils.isEmpty(value)
				? new String[] { "scw.logger.log4j.Log4jLoggerFactory", "scw.logger.log4j2.Log4j2LoggerFactory" }
				: StringUtils.commonSplit(value);
	}

	static {
		ILoggerFactory loggerFactory = null;
		for (String name : getSupperLoggerFactory()) {
			loggerFactory = InstanceUtils.getInstance(name);
			if (loggerFactory != null) {
				break;
			}
		}

		LOGGER_FACTORY = loggerFactory == null ? new AsyncConsoleLoggerFactory() : loggerFactory;
		FormatUtils.info(LoggerFactory.class, "Init shuchaowen-logger [{}]", LOGGER_FACTORY.getClass().getName());
	}

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
