package scw.logger;

import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

public final class LoggerFactory {
	private static final ILoggerFactory LOGGER_FACTORY;

	private LoggerFactory() {
	};
	
	private static String[] getSupperLoggerFactory(){
		String value = SystemPropertyUtils.getProperty("scw.logger.factory");
		return StringUtils.isEmpty(value)? new String[]{
			"scw.logger.log4j.Log4jLoggerFactory"
		}:StringUtils.commonSplit(value); 
	}

	static {
		ILoggerFactory loggerFactory = null;
		for (String name : getSupperLoggerFactory()) {
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
