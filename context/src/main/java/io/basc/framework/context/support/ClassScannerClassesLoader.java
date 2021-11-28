package io.basc.framework.context.support;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.core.type.scanner.ClassScanner;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.StaticSupplier;
import io.basc.framework.util.StringUtils;

public class ClassScannerClassesLoader extends AbstractClassesLoader {
	private static Logger logger = LoggerFactory.getLogger(ClassScannerClassesLoader.class);
	private final ClassScanner classScanner;
	private final Supplier<String> packageName;
	private final TypeFilter typeFilter;
	private final Predicate<Class<?>> predicate;
	
	public ClassScannerClassesLoader(ClassScanner classScanner, ClassLoaderProvider classLoaderProvider,
			String packageName, TypeFilter typeFilter) {
		this(classScanner, classLoaderProvider, packageName, typeFilter, null);
	}

	public ClassScannerClassesLoader(ClassScanner classScanner, ClassLoaderProvider classLoaderProvider,
			String packageName, TypeFilter typeFilter, Predicate<Class<?>> predicate) {
		this(classScanner, classLoaderProvider, new StaticSupplier<String>(packageName), typeFilter, predicate);
	}

	public ClassScannerClassesLoader(ClassScanner classScanner, ClassLoaderProvider classLoaderProvider,
			Supplier<String> packageName, @Nullable TypeFilter typeFilter, @Nullable Predicate<Class<?>> predicate) {
		this.classScanner = classScanner;
		this.packageName = packageName;
		this.typeFilter = typeFilter;
		this.predicate = predicate;
		setClassLoaderProvider(classLoaderProvider);
	}

	@Override
	protected Set<Class<?>> getClasses(ClassLoader classLoader) {
		String packageName = this.packageName.get();
		if (StringUtils.isEmpty(packageName)) {
			return Collections.emptySet();
		}

		long t = System.currentTimeMillis();
		Set<Class<?>> classes = classScanner.getClasses(packageName, classLoader, typeFilter, predicate);
		if (logger.isDebugEnabled()) {
			logger.debug("scanner package " + packageName + " use time " + (System.currentTimeMillis() - t) + "ms");
		}
		return classes;
	}
}
