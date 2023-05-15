package io.basc.framework.util;

import java.util.function.Predicate;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public class ResourceClassesLoader extends DefaultClassLoaderAccessor implements ServiceLoader<Class<?>> {
	public static final String FILE_SUFFIX = ".class";

	private static Logger logger = LoggerFactory.getLogger(ResourceClassesLoader.class);
	private volatile MetadataReaderFactory metadataReaderFactory;

	private final Elements<? extends Resource> resources;

	private TypeFilter typeFilter;

	private Predicate<? super Class<?>> classFilter;

	/**
	 * 不进行反射校验
	 */
	private boolean notPerformReflectionVerification = Boolean.getBoolean("perform.reflection.verification.disable");

	public ResourceClassesLoader(Elements<Resource> resources) {
		Assert.requiredArgument(resources != null, "resources");
		this.resources = resources;
	}

	public MetadataReaderFactory getMetadataReaderFactory() {
		if (metadataReaderFactory == null) {
			synchronized (this) {
				if (metadataReaderFactory == null) {
					metadataReaderFactory = new CachingMetadataReaderFactory(getClassLoader());
				}
			}
		}
		return metadataReaderFactory;
	}

	@Override
	public Elements<Class<?>> getServices() {
		Elements<Class<?>> elements = resources.map((resource) -> {
			if (resource == null) {
				return null;
			}

			MetadataReaderFactory factory = getMetadataReaderFactory();
			try {
				MetadataReader reader = factory.getMetadataReader(resource);
				if (reader == null) {
					return null;
				}

				TypeFilter typeFilter = getTypeFilter();
				if (typeFilter != null && !typeFilter.match(reader, factory)) {
					return null;
				}

				Class<?> clazz = ClassUtils.getClass(reader.getClassMetadata().getClassName(), getClassLoader());
				if (clazz == null) {
					return null;
				}

				Predicate<? super Class<?>> classFilter = getClassFilter();
				if (classFilter != null && !classFilter.test(clazz)) {
					return null;
				}

				// 反射校验相关类是否可用
				if (!isNotPerformReflectionVerification() && !ReflectionUtils.isAvailable(clazz, logger)) {
					return null;
				}

				return clazz;
			} catch (Throwable e) {
				logger.error(e, "Failed to load class from resource {}", resource);
				return null;
			}
		});
		return elements.filter((e) -> e != null);
	}

	public Elements<? extends Resource> getResources() {
		return resources;
	}

	public TypeFilter getTypeFilter() {
		return typeFilter;
	}

	public Predicate<? super Class<?>> getClassFilter() {
		return classFilter;
	}

	public void setClassFilter(Predicate<? super Class<?>> classFilter) {
		this.classFilter = classFilter;
	}

	public boolean isNotPerformReflectionVerification() {
		return notPerformReflectionVerification;
	}

	public void setNotPerformReflectionVerification(boolean notPerformReflectionVerification) {
		this.notPerformReflectionVerification = notPerformReflectionVerification;
	}

	@Override
	public void reload() {
		if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
			((CachingMetadataReaderFactory) metadataReaderFactory).clearCache();
		}
	}

	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
		this.metadataReaderFactory = metadataReaderFactory;
	}

	public void setTypeFilter(TypeFilter typeFilter) {
		this.typeFilter = typeFilter;
	}
}
