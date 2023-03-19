package io.basc.framework.io;

import java.util.function.Supplier;

import io.basc.framework.util.StaticSupplier;

/**
 * 扫描classpath下的类文件
 * 
 * @author wcnnkh
 *
 */
public class ClassPathClassesLoader extends LocationClassesLoader {

	public static String cleanPath(String path) {
		return (path.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)
				|| path.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) ? path
						: (ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path);
	}

	public ClassPathClassesLoader(ResourcePatternResolver resourcePatternResolver, String classPath) {
		this(resourcePatternResolver, new StaticSupplier<>(classPath));
	}

	public ClassPathClassesLoader(ResourcePatternResolver resourcePatternResolver, Supplier<String> classPathSupplier) {
		super(resourcePatternResolver, () -> {
			String path = classPathSupplier.get();
			if (path == null) {
				return null;
			}

			return ClassPathClassesLoader.cleanPath(path);
		});
	}

}
