package scw.core.utils;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import scw.core.resource.ResourceUtils;

public class SystemUtils {
	private static final boolean IS_JAR;// 当前是否已jar的方式运行

	private SystemUtils() {
	};

	static {
		URL classPath = getClassPathURL();
		if (classPath == null) {
			IS_JAR = true;
		} else {
			File file = new File(classPath.getPath());
			IS_JAR = (file == null || !file.exists() || file.isFile());
		}
	}

	/**
	 * @return 结果可能为空
	 */
	public static URL getClassPathURL() {
		URL url = ResourceUtils.class.getResource("/");
		if (url == null) {
			ProtectionDomain protectionDomain = ResourceUtils.class.getProtectionDomain();
			if (protectionDomain != null) {
				CodeSource codeSource = protectionDomain.getCodeSource();
				if (codeSource != null) {
					url = codeSource.getLocation();
				}
			}
		}
		return url;
	}

	/**
	 * @return 当前是否已jar的方式运行
	 */
	public static final boolean isJar() {
		return IS_JAR;
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
