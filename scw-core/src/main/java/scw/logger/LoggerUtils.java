package scw.logger;

public final class LoggerUtils {
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
}
