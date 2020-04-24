package scw.logger;

import java.lang.reflect.Method;

import scw.core.reflect.ReflectionUtils;
import scw.lang.NestedRuntimeException;

public final class LoggerUtils {
	private static final ConsoleLoggerFactory CONSOLE_LOGGER_FACTORY = new ConsoleLoggerFactory();
	private static Method I_LOGGER_FACTORY_METHOD = ReflectionUtils.getMethod("scw.logger.LoggerFactory",
			"getILoggerFactory");

	private LoggerUtils() {
	};

	/**
	 * 此方法获取的logger是延迟加载的
	 * 
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz) {
		return new LazyLogger(clazz.getName(), null);
	}

	/**
	 * 此方法获取的logger是延迟加载的
	 * 
	 * @param clazz
	 * @param placeholder
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz, String placeholder) {
		return new LazyLogger(clazz.getName(), placeholder);
	}

	/**
	 * 此方法获取的logger是延迟加载的
	 * 
	 * @param name
	 * @return
	 */
	public static Logger getLogger(String name) {
		return new LazyLogger(name, null);
	}

	/**
	 * 此方法获取的logger是延迟加载的
	 * 
	 * @param name
	 * @param placeholder
	 * @return
	 */
	public static Logger getLogger(String name, String placeholder) {
		return new LazyLogger(name, placeholder);
	}

	public static Logger getConsoleLogger(Class<?> clazz) {
		return CONSOLE_LOGGER_FACTORY.getLogger(clazz.getName());
	}

	public static Logger getConsoleLogger(Class<?> clazz, String placeholder) {
		return CONSOLE_LOGGER_FACTORY.getLogger(clazz.getName(), placeholder);
	}

	public static Logger getConsoleLogger(String name) {
		return CONSOLE_LOGGER_FACTORY.getLogger(name);
	}

	public static Logger getConsoleLogger(String name, String placeholder) {
		return CONSOLE_LOGGER_FACTORY.getLogger(name, placeholder);
	}

	public static ILoggerFactory getILoggerFactory() {
		try {
			return (ILoggerFactory) I_LOGGER_FACTORY_METHOD.invoke(null);
		} catch (Exception e) {
			throw new NestedRuntimeException(e);
		}
	}

	public static boolean isLoggerEnabled(Logger logger, Level level) {
		switch (level) {
		case INFO:
			return logger.isInfoEnabled();
		case DEBUG:
			return logger.isDebugEnabled();
		case ERROR:
			return logger.isErrorEnabled();
		case TRACE:
			return logger.isTraceEnabled();
		case WARN:
			return logger.isWarnEnabled();
		default:
			return false;
		}
	}

	public static void logger(Logger logger, Level level, final Object format, final Object... args) {
		logger(logger, level, null, format, args);
	}

	public static void logger(Logger logger, Level level, final Throwable e, final Object format,
			final Object... args) {
		switch (level) {
		case INFO:
			logger.info(e, format, args);
			break;
		case DEBUG:
			logger.debug(e, format, args);
			break;
		case ERROR:
			logger.error(e, format, args);
			break;
		case TRACE:
			logger.trace(e, format, args);
			break;
		case WARN:
			logger.warn(e, format, args);
			break;
		default:
			break;
		}
	}
}
