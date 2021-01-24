package scw.context.support;

import java.util.Set;

import scw.context.ClassScanner;
import scw.core.type.filter.TypeFilter;
import scw.util.ClassLoaderProvider;

public final class ClassScannerClassesLoader<S> extends
		AbstractClassesLoader<S> {
	private final ClassScanner classScanner;
	private final String packageName;
	private final TypeFilter typeFilter;

	public ClassScannerClassesLoader(ClassScanner classScanner,
			ClassLoaderProvider classLoaderProvider, String packageName,
			TypeFilter typeFilter) {
		this.classScanner = classScanner;
		this.packageName = packageName;
		this.typeFilter = typeFilter;
		setClassLoaderProvider(classLoaderProvider);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Set<Class<S>> getClasses(ClassLoader classLoader) {
		Set classes = classScanner.getClasses(packageName, classLoader,
				typeFilter);
		return classes;
	}
}
