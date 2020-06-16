package scw.logger;

import scw.core.instance.InstanceUtils;
import scw.util.FormatUtils;

public final class LoggerFactory {
	private static final ILoggerFactory LOGGER_FACTORY = InstanceUtils.getSystemConfiguration(ILoggerFactory.class);

	private LoggerFactory() {
	};

	static {
		if(LOGGER_FACTORY != null){
			FormatUtils.info(LoggerFactory.class, "using logger factory [{}]", LOGGER_FACTORY.getClass().getName());
		}
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
