package io.basc.framework.util;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.basc.framework.io.FileUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.concurrent.ThreadPerTaskExecutor;

public final class XUtils {
	private static final boolean ENABLE_COMMON_POOL = (ForkJoinPool.getCommonPoolParallelism() > 1);
	/**
	 * Default executor -- ForkJoinPool.commonPool() unless it cannot support
	 * parallelism.
	 */
	private static final Executor COMMON_EXECUTOR = ENABLE_COMMON_POOL ? ForkJoinPool.commonPool()
			: new ThreadPerTaskExecutor();

	private XUtils() {
	};

	public static boolean isEnableCommonPool() {
		return ENABLE_COMMON_POOL;
	}

	public static Executor getCommonExecutor() {
		return COMMON_EXECUTOR;
	}

	/**
	 * 获取UUID，已经移除了‘-’
	 * 
	 * @return
	 */
	public static String getUUID() {
		return StringUtils.removeChar(UUID.randomUUID().toString(), '-');
	}

	/**
	 * @see Decorator#getDelegate(Class)
	 * @param wrapper
	 * @param targetType
	 * @return
	 */
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

	/**
	 * 将一次迭代变为操作流
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> Stream<T> stream(Iterator<? extends T> iterator) {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		return StreamSupport.stream(spliterator, false);
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

	public static <E extends Throwable> RunnableProcessor<E> composeWithExceptions(RunnableProcessor<? extends E> a,
			RunnableProcessor<? extends E> b) {
		return () -> {
			try {
				a.process();
			} catch (Throwable e1) {
				try {
					b.process();
				} catch (Throwable e2) {
					try {
						e1.addSuppressed(e2);
					} catch (Throwable ignore) {
					}
				}
				throw e1;
			}
			b.process();
		};
	}

	public static <T> Stream<T> emptyStream() {
		List<T> list = Collections.emptyList();
		return list.stream();
	}
}