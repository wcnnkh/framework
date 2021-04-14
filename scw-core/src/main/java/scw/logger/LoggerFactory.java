package scw.logger;

import java.util.ServiceLoader;

import scw.core.utils.CollectionUtils;

public final class LoggerFactory {
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoggerFactory.class.getName());
	private static final ILoggerFactory LOGGER_FACTORY;
	
	static{
		ServiceLoader<ILoggerFactory> serviceLoader = ServiceLoader.load(ILoggerFactory.class);
		ILoggerFactory loggerFactory = CollectionUtils.first(serviceLoader);
		LOGGER_FACTORY = loggerFactory == null? new JdkLoggerFactory() : loggerFactory;
		logger.info("Use logger factory ["+LOGGER_FACTORY+"]");
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
