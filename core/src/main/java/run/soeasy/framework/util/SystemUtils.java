package run.soeasy.framework.util;

import java.io.File;
import java.net.URL;

import lombok.NonNull;

public final class SystemUtils {
	private SystemUtils() {
	}

	public static CharSequenceTemplate getClassPath() {
		return new CharSequenceTemplate(System.getProperty("java.class.path"), getPathSeparator());
	}

	/**
	 * 获取环境变量分割符
	 * 
	 * @return
	 */
	public static String getPathSeparator() {
		return System.getProperty("path.separator");
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

	/**
	 * 先从{@link System#getProperty(String)}中获取，如果为空就从{@link System#getenv(String)}中获取
	 * 
	 * @param name
	 * @return
	 */
	public static String getProperty(@NonNull String name) {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(name);
		}
		return value;
	}

	public static String getTempDirectory() {
		return System.getProperty("java.io.tmpdir");
	}

	public static String getUserHome() {
		return System.getProperty("user.home");
	}

	public static String getUserDir() {
		return System.getProperty("user.dir");
	}

	/**
	 * 获取运行时所在的目录
	 * 
	 * @param classLoader
	 * @return
	 */
	public static String getRuntimeDirectory(ClassLoader classLoader) {
		URL url = classLoader.getResource("");
		return url == null ? getUserDir() : url.getPath();
	}

	/**
	 * 获取webapp目录
	 * 
	 * @param classLoader
	 * @return
	 */
	public static String getWebAppDirectory(ClassLoader classLoader) {
		// /xxxxx/{project}/target/classes
		String path = getRuntimeDirectory(classLoader);
		File file = new File(path);
		if (file.isFile()) {
			return null;
		}

		if (!file.getName().equals("classes")) {
			return path;
		}

		for (int i = 0; i < 2; i++) {
			file = file.getParentFile();
			if (file == null) {
				return path;
			}

			if (file.getName().equals("WEB-INF") && file.getParent() != null) {
				return file.getParent();
			}
		}

		if (file != null) {
			File webapp = new File(file, "/src/main/webapp");
			if (webapp.exists()) {
				return webapp.getPath();
			}
			/*
			 * //可能会出现一个bin目录，忽略此目录 final File binDirectory = new File(file, "bin"); //
			 * 路径/xxxx/src/main/webapp/WEB-INF 4层深度 File wi = FileUtils.search(file, new
			 * Accept<File>() {
			 * 
			 * public boolean accept(File e) { if(e.isDirectory() &&
			 * "WEB-INF".equals(e.getName())){ //可能会出现一个bin目录，忽略此目录 if(binDirectory.exists()
			 * && binDirectory.isDirectory() &&
			 * e.getPath().startsWith(binDirectory.getPath())){ return false; } return true;
			 * } return false; } }, 4); if (wi != null) { return wi.getParent(); }
			 */
		}
		return path;
	}
}
