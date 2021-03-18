package scw.context.support;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import scw.core.type.classreading.MetadataReader;
import scw.core.type.classreading.MetadataReaderFactory;
import scw.core.type.filter.TypeFilter;
import scw.core.type.scanner.ClassScanner;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.ClassLoaderProvider;
import scw.util.StaticSupplier;
import scw.util.Supplier;

public class ClassScannerClassesLoader<S> extends
		AbstractClassesLoader<S> implements TypeFilter{
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Set<Class<S>> getClasses(ClassLoader classLoader) {
		String packageName = this.packageName.get();
		if(StringUtils.isEmpty(packageName)){
			return Collections.emptySet();
		}

		long t = System.currentTimeMillis();
		Set classes = classScanner.getClasses(packageName, classLoader, this);
		if(logger.isDebugEnabled()){
			logger.debug("scanner package " + packageName + " use time " + (System.currentTimeMillis() - t) + "ms");
		}
		return classes;
	}
}
