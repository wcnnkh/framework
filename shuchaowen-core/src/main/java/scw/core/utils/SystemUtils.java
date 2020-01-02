package scw.core.utils;

public class SystemUtils {
	private SystemUtils() {
	};

	public static String getProperty(String key) {
		String v = System.getProperty(key);
		if (v == null) {
			v = System.getenv(key);
		}
		return v;
	}

	/**
	 * 获取系统可用处理器数量
	 * 
	 * @return
	 */
	public static int getAvailableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}
}
