package scw.core.utils;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class SystemUtils {
	private SystemUtils() {
	};

	/**
	 * @return 结果可能为空
	 */
	public static URL getClassPathURL() {
		ProtectionDomain protectionDomain = SystemUtils.class.getProtectionDomain();
		if (protectionDomain != null) {
			CodeSource codeSource = protectionDomain.getCodeSource();
			if (codeSource != null) {
				return codeSource.getLocation();
			}
		}
		return null;
	}

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
