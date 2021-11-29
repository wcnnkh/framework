package io.basc.framework.context.support;

import java.io.IOException;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.StaticSupplier;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.StreamProcessorSupport;

public class PackageClassesLoader extends AbstractClassesLoader {
	private final ResourcePatternResolver resourcePatternResolver;
	private final Supplier<String> packageName;
	private final TypeFilter typeFilter;

	public PackageClassesLoader(ResourcePatternResolver resourcePatternResolver, String packageName,
			@Nullable ClassLoaderProvider classLoaderProvider, @Nullable TypeFilter typeFilter) {
		this(resourcePatternResolver, new StaticSupplier<String>(packageName), classLoaderProvider, typeFilter);
	}

	public PackageClassesLoader(ResourcePatternResolver resourcePatternResolver, Supplier<String> packageName,
			@Nullable ClassLoaderProvider classLoaderProvider, @Nullable TypeFilter typeFilter) {
		this.resourcePatternResolver = resourcePatternResolver;
		this.packageName = packageName;
		this.typeFilter = typeFilter;
		setClassLoaderProvider(classLoaderProvider);
	}

	public ResourcePatternResolver getResourcePatternResolver() {
		return resourcePatternResolver;
	}

	public Supplier<String> getPackageName() {
		return packageName;
	}

	public TypeFilter getTypeFilter() {
		return typeFilter;
	}

	@Override
	protected Stream<Class<?>> load(ClassLoader classLoader) {
		String packageName = this.packageName.get();
		if (StringUtils.isEmpty(packageName)) {
			return StreamProcessorSupport.emptyStream();
		}

		Stream<Class<?>> stream;
		try {
			stream = ResourceUtils.getClassesByPckageName(resourcePatternResolver, packageName, classLoader, null,
					typeFilter);
		} catch (IOException e) {
			logger.error(e, "Scan package {}", packageName);
			return StreamProcessorSupport.emptyStream();
		}
		return stream;
	}

	@Override
	public Set<Class<?>> getClasses(ClassLoader classLoader) {
		long t = System.currentTimeMillis();
		Set<Class<?>> classes = super.getClasses(classLoader);
		if (logger.isDebugEnabled()) {
			logger.debug("scanner package {}[{}] use time {}ms", packageName.get(), classes.size(),
					(System.currentTimeMillis() - t));
		}
		return classes;
	}
}
