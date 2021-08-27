package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.core.type.scanner.ClassScanner;
import io.basc.framework.core.utils.ClassUtils;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ConcurrentReferenceHashMap;

import java.io.IOException;

public class DefaultClassesLoaderFactory implements ClassesLoaderFactory, TypeFilter {
	private ConcurrentReferenceHashMap<String, ClassesLoader> cacheMap;
	private ClassLoaderProvider classLoaderProvider;
	private final ClassScanner classScanner;

	public DefaultClassesLoaderFactory(ClassScanner classScanner, boolean cache,
			@Nullable ClassLoaderProvider classLoaderProvider) {
		this.classScanner = classScanner;
		if (cache) {
			cacheMap = new ConcurrentReferenceHashMap<String, ClassesLoader>();
		}
		this.classLoaderProvider = classLoaderProvider;
	}

	private ClassesLoader getClassesLoaderInternal(final String packageName) {
		if (cacheMap == null) {
			return new ClassScannerClassesLoader(classScanner, this, packageName, this);
		} else {
			ClassesLoader classesLoader = cacheMap.get(packageName);
			if (classesLoader != null) {
				return classesLoader;
			}

			String[] parentPackageNames = ClassUtils.getParentPackageNames(packageName);
			if (parentPackageNames.length != 0) {
				for (int len = parentPackageNames.length, i = len - 1; i >= 0; i--) {
					ClassesLoader classes = cacheMap.get(parentPackageNames[i]);
					if (classes == null) {
						continue;
					}

					return new AcceptClassesLoader(classes, new Accept<Class<?>>() {
						public boolean accept(Class<?> e) {
							return e.getName().startsWith(packageName);
						}
					}, false);
				}
			}

			classesLoader = new ClassScannerClassesLoader(classScanner, this, packageName, this);
			ClassesLoader cache = cacheMap.putIfAbsent(packageName, classesLoader);
			if (cache != null) {
				classesLoader = cache;
			}
			return classesLoader;
		}
	}

	public ClassesLoader getClassesLoader(final String packageName) {
		String[] packageNames = StringUtils.commonSplit(packageName);
		DefaultClassesLoader editableClassesLoader = new DefaultClassesLoader();
		for (String name : packageNames) {
			editableClassesLoader.add((ClassesLoader) getClassesLoaderInternal(name));
		}
		return editableClassesLoader;
	}

	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		return true;
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}
}
