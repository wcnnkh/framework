package io.basc.framework.util;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.basc.framework.io.FileUtils;
import io.basc.framework.lang.Nullable;

public final class XUtils {
	private XUtils() {
	};

	/**
	 * 获取UUID，已经移除了‘-’
	 * 
	 * @return
	 */
	public static String getUUID() {
		return StringUtils.removeChar(UUID.randomUUID().toString(), '-');
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T getDelegate(Object wrapper, Class<T> targetType) {
		if (targetType.isInstance(wrapper)) {
			return (T) wrapper;
		}

		if (wrapper instanceof Decorator) {
			return ((Decorator) wrapper).getDelegate(targetType);
		}
		return null;
	}

	public static Comparator<String> getComparator(final StringMatcher matcher) {
		return new Comparator<String>() {

			public int compare(String o1, String o2) {
				if (matcher.isPattern(o1) && matcher.isPattern(o2)) {
					if (matcher.match(o1, o2)) {
						return 1;
					} else if (matcher.match(o2, o1)) {
						return -1;
					} else {
						return -1;
					}
				} else if (matcher.isPattern(o1)) {
					return 1;
				} else if (matcher.isPattern(o2)) {
					return -1;
				}
				return o1.equals(o1) ? 0 : -1;
			}
		};
	}

	/**
	 * 获取名称
	 * 
	 * @see Named#getName()
	 * @param instance
	 * @param defaultName
	 * @return
	 */
	public static String getName(Object instance, String defaultName) {
		if (instance == null) {
			return defaultName;
		}

		if (instance instanceof Named) {
			String name = ((Named) instance).getName();
			return name == null ? defaultName : name;
		}

		return defaultName;
	}

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

	public static <T> Status<T> status(boolean active, T value) {
		return new DefaultStatus<T>(active, value);
	}

	/**
	 * 将一次迭代变为操作流
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> Stream<T> stream(Iterator<T> iterator) {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		return StreamSupport.stream(spliterator, false);
	}

	/**
	 * 是否是一个有多个值的类型
	 * 
	 * @see Collection
	 * @see Array
	 * @param type
	 * @return
	 */
	public static boolean isMultipleValues(Class<?> type) {
		return type != null && (type.isArray() || Collection.class.isAssignableFrom(type));
	}
}