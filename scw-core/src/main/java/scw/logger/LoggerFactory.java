package scw.logger;

import scw.instance.InstanceUtils;

public final class LoggerFactory {
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoggerFactory.class.getName());
	private static final ILoggerFactory LOGGER_FACTORY;
	
	static{
		ILoggerFactory loggerFactory = InstanceUtils.loadService(ILoggerFactory.class);
		LOGGER_FACTORY = loggerFactory == null? new AsyncConsoleLoggerFactory() : loggerFactory;
		logger.info("Use logger factory ["+loggerFactory+"]");
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

	public static ILoggerFactory getILoggerFactory() {
		return LOGGER_FACTORY;
	}
}
