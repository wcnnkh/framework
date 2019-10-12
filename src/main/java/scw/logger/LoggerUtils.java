package scw.logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.resource.ResourceUtils;
import scw.core.utils.StringAppend;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XTime;

public final class LoggerUtils {
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	private static final Map<String, Level> LOGGER_LEVEL = new HashMap<String, Level>();

	static {
		String loggerEnablePropertiePath = SystemPropertyUtils.getProperty("scw.logger.enable");
		if (loggerEnablePropertiePath == null) {
			loggerEnablePropertiePath = "classpath:/logger-enable.properties";
		}

		if (ResourceUtils.isExist(loggerEnablePropertiePath)) {
			Properties properties = ResourceUtils.getProperties(loggerEnablePropertiePath);
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Object key = entry.getKey();
				if (key == null) {
					continue;
				}

				Object value = entry.getValue();
				if (value == null) {
					continue;
				}

				Level level = Level.valueOf(value.toString().toUpperCase());
				if (level == null) {
					continue;
				}

				LOGGER_LEVEL.put(key.toString(), level);
			}
		}
	}

	private LoggerUtils() {
	};

	public static Class<?> init() {
		try {
			return Class.forName("scw.logger.LoggerFactory");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("初始化日志工厂失败", e);
		}
	}

	public static void setLoggerLevel(String name, Level level) {
		LOGGER_LEVEL.put(name, level);
	}

	public static Level getLoggerLevel(String name) {
		Level level = LOGGER_LEVEL.get(name);
		return level == null ? Level.TRACE : level;
	}

	public static void loggerAppend(Appendable appendable, String time, String level, String tag,
			StringAppend stringAppend) throws Exception {
		boolean b = false;
		if (!StringUtils.isEmpty(time)) {
			appendable.append(time);
			b = true;
		}

		if (!StringUtils.isEmpty(level)) {
			if (b) {
				appendable.append(" ");
			}
			b = true;
			appendable.append(level);
		}

		if (!StringUtils.isEmpty(tag)) {
			if (b) {
				appendable.append(" ");
			}
			b = true;
			appendable.append("[");
			appendable.append(tag);
			appendable.append("]");
		}

		if (stringAppend != null) {
			if (b) {
				appendable.append(" - ");
			}
			b = true;
			stringAppend.appendTo(appendable);
		}
	}

	public static void loggerAppend(Appendable appendable, long time, String level, String tag,
			StringAppend stringAppend) throws Exception {
		loggerAppend(appendable, XTime.format(time, TIME_FORMAT), level, tag, stringAppend);
	}

	public static void loggerAppend(Appendable appendable, long time, String level, String tag, String placeholder,
			String msg, Object... args) throws Exception {
		StringAppend loggerAppend = new DefaultLoggerFormatAppend(msg, placeholder, args);
		loggerAppend(appendable, time, level, tag, loggerAppend);
	}

	public static void info(Class<?> clazz, String msg, Object... args) {
		StringBuilder sb = new StringBuilder(256);
		try {
			loggerAppend(sb, System.currentTimeMillis(), "CONSOLE", clazz.getName(), null, msg, args);
			System.out.println(sb.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void warn(Class<?> clazz, String msg, Object... args) {
		StringBuilder sb = new StringBuilder(256);
		try {
			loggerAppend(sb, System.currentTimeMillis(), "CONSOLE", clazz.getName(), null, msg, args);
			System.err.println(sb.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Boolean defaultConfigEnable() {
		return StringUtils.parseBoolean(SystemPropertyUtils.getProperty("scw.logger.default.config.enable"), null);
	}

	public static void setDefaultConfigenable(boolean enable) {
		SystemPropertyUtils.setPrivateProperty("scw.logger.default.config.enable", enable + "");
	}

	/**
	 * 此方法获取的logger是延迟加载的
	 * 
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz) {
		return new LazyLogger(clazz);
	}

	/**
	 * 此方法获取的logger是延迟加载的
	 * 
	 * @param clazz
	 * @param placeholder
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz, String placeholder) {
		return new LazyLogger(clazz, placeholder);
	}

	/**
	 * 此方法获取的logger是延迟加载的
	 * 
	 * @param name
	 * @return
	 */
	public static Logger getLogger(String name) {
		return new LazyLogger(name);
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
}
