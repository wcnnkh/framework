package scw.logger;

import java.lang.reflect.Method;

import scw.core.reflect.ReflectionUtils;
import scw.lang.NestedRuntimeException;

public final class LoggerUtils {
	private static final ConsoleLoggerFactory CONSOLE_LOGGER_FACTORY = new ConsoleLoggerFactory();
	private static Method I_LOGGER_FACTORY_METHOD = ReflectionUtils.getMethod(
			"scw.logger.LoggerFactory", "getILoggerFactory");

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
}
