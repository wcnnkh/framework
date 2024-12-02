package io.basc.framework.util;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.io.FileUtils;

public final class XUtils {

	private static final Predicate<?> ALWAYS_TRUE_PREDICATE = (e) -> true;

	private XUtils() {
	};

	/**
	 * 获取运行时所在的目录
	 * 
	 * @param classLoader
	 * @return
	 */
	public static String getRuntimeDirectory(ClassLoader classLoader) {
		URL url = classLoader.getResource("");
		return url == null ? FileUtils.getUserDir() : url.getPath();
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

	public static String getClassPath() {
		return System.getProperty("java.class.path");
	}

	/**
	 * 获取环境变量分割符
	 * 
	 * @return
	 */
	public static String getPathSeparator() {
		return System.getProperty("path.separator");
	}

	public static String[] getClassPathArray() {
		String classPath = getClassPath();
		if (StringUtils.isEmpty(classPath)) {
			return null;
		}

		return StringUtils.splitToArray(classPath, getPathSeparator());
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

	public static Object toString(Supplier<String> supplier) {
		return new StringObject(supplier);
	}

	private static class StringObject implements Serializable {
		private static final long serialVersionUID = 1L;
		private final Supplier<String> msg;

		public StringObject(Supplier<String> msg) {
			this.msg = msg;
		}

		@Override
		public String toString() {
			return msg.get();
		}
	}

	@SafeVarargs
	public static <T> T find(Predicate<? super T> predicate, T... option) {
		if (option == null) {
			return null;
		}

		for (T o : option) {
			if (predicate.test(o)) {
				return o;
			}
		}
		return null;
	}

	/**
	 * 永远返回true的Predicate
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> alwaysTruePredicate() {
		return (Predicate<T>) ALWAYS_TRUE_PREDICATE;
	}
}