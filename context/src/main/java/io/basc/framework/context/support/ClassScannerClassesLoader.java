package io.basc.framework.context.support;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.core.type.scanner.ClassScanner;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.StaticSupplier;
import io.basc.framework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class ClassScannerClassesLoader extends
		AbstractClassesLoader implements TypeFilter{
	private static Logger logger = LoggerFactory.getLogger(ClassScannerClassesLoader.class);
	private final ClassScanner classScanner;
	private final Supplier<String> packageName;
	private final TypeFilter typeFilter;

	public ClassScannerClassesLoader(ClassScanner classScanner,
			ClassLoaderProvider classLoaderProvider, String packageName,
			TypeFilter typeFilter) {
		this(classScanner, classLoaderProvider, new StaticSupplier<String>(packageName), typeFilter);
	}
	
	public ClassScannerClassesLoader(ClassScanner classScanner,
			ClassLoaderProvider classLoaderProvider, Supplier<String> packageName,
			TypeFilter typeFilter) {
		this.classScanner = classScanner;
		this.packageName = packageName;
		this.typeFilter = typeFilter;
		setClassLoaderProvider(classLoaderProvider);
	}
	
	@Override
	public boolean match(MetadataReader metadataReader,
			MetadataReaderFactory metadataReaderFactory) throws IOException {
		return super.match(metadataReader, metadataReaderFactory) && (typeFilter == null || typeFilter.match(metadataReader, metadataReaderFactory));
	}

	@Override
	protected Set<Class<?>> getClasses(ClassLoader classLoader) {
		String packageName = this.packageName.get();
		if(StringUtils.isEmpty(packageName)){
			return Collections.emptySet();
		}

		long t = System.currentTimeMillis();
		Set<Class<?>> classes = classScanner.getClasses(packageName, classLoader, this);
		if(logger.isDebugEnabled()){
			logger.debug("scanner package " + packageName + " use time " + (System.currentTimeMillis() - t) + "ms");
		}
		return classes;
	}
}
