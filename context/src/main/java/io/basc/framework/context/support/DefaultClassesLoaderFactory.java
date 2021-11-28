package io.basc.framework.context.support;

import java.util.function.Predicate;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.core.type.scanner.ClassScanner;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;

public class DefaultClassesLoaderFactory implements ClassesLoaderFactory, Predicate<Class<?>> {
	private static Logger logger = LoggerFactory.getLogger(DefaultClassesLoaderFactory.class);
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
			editableClassesLoader.add(new ClassScannerClassesLoader(classScanner, this, name, typeFilter, this));
		}
		return editableClassesLoader;
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	@Override
	public boolean test(Class<?> c) {
		return ClassUtils.isAvailable(c) && ReflectionUtils.isAvailable(c, (e) -> {
			if (logger.isTraceEnabled()) {
				logger.trace(e, "This class[{}] cannot be included because:", c.getName());
			} else if (logger.isDebugEnabled()) {
				logger.debug("This class[{}] cannot be included because {}: {}", c.getName(),
						NestedExceptionUtils.getRootCause(e).getClass(),
						NestedExceptionUtils.getNonEmptyMessage(e, false));
			}
			return false;
		});
	}
}
