package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.core.type.scanner.ClassScanner;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;

public class DefaultClassesLoaderFactory implements ClassesLoaderFactory {
	private ClassLoaderProvider classLoaderProvider;
	private final ClassScanner classScanner;

	public DefaultClassesLoaderFactory(ClassScanner classScanner) {
		this(classScanner, null);
	}

	public DefaultClassesLoaderFactory(ClassScanner classScanner, @Nullable ClassLoaderProvider classLoaderProvider) {
		this.classScanner = classScanner;
		this.classLoaderProvider = classLoaderProvider;
	}

	public ClassesLoader getClassesLoader(final String packageName, TypeFilter typeFilter) {
		String[] packageNames = StringUtils.splitToArray(packageName);
		DefaultClassesLoader editableClassesLoader = new DefaultClassesLoader();
		for (String name : packageNames) {
			editableClassesLoader.add(new ClassScannerClassesLoader(classScanner, this, name, typeFilter));
		}
		return editableClassesLoader;
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}
}
