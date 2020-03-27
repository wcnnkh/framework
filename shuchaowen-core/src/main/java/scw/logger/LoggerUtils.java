package scw.logger;

import java.lang.reflect.Method;

import scw.core.GlobalPropertyFactory;
import scw.core.utils.StringUtils;

public final class LoggerUtils {
	private static final ConsoleLoggerFactory CONSOLE_LOGGER_FACTORY = new ConsoleLoggerFactory();

	private LoggerUtils() {
	};

	public static Class<?> init() {
		try {
			return Class.forName("scw.logger.LoggerFactory");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("初始化日志工厂失败", e);
		}
	}

	public static Boolean defaultConfigEnable() {
		return StringUtils.parseBoolean(GlobalPropertyFactory.getInstance().getString("scw.logger.default.config.enable"), null);
	}

	public static void setDefaultConfigenable(boolean enable) {
		GlobalPropertyFactory.getInstance().put("scw.logger.default.config.enable", enable + "");
	}

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

	public static void destroy() {
		Class<?> clazz = init();
		try {
			Method method = clazz.getDeclaredMethod("destroy");
			method.invoke(null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
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
}
