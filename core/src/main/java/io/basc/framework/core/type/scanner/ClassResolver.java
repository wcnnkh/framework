package io.basc.framework.core.type.scanner;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.classreading.SimpleMetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.JavaVersion;

public class ClassResolver {
	private MetadataReaderFactory metadataReaderFactory;

	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
		this.metadataReaderFactory = metadataReaderFactory;
	}

	public MetadataReaderFactory getMetadataReaderFactory() {
		return metadataReaderFactory;
	}

	public void resolve(Collection<Resource> resources, ResourceLoader resourceLoader, ClassLoader classLoader,
			@Nullable TypeFilter typeFilter, Predicate<Class<?>> predicate) {
		if (CollectionUtils.isEmpty(resources)) {
			return;
		}

		MetadataReaderFactory metadataReaderFactory = this.metadataReaderFactory;
		if (metadataReaderFactory == null) {
			metadataReaderFactory = new SimpleMetadataReaderFactory(resourceLoader,
					new DefaultClassLoaderProvider(classLoader));
		}

		for (Resource resource : resources) {
			try {
				Class<?> clazz = resolve(resource, classLoader, metadataReaderFactory, typeFilter);
				if (clazz == null) {
					continue;
				}

				if (!predicate.test(clazz)) {
					break;
				}
			} catch (IOException e) {
			}
		}
		return;
	}

	public void resolve(Collection<Resource> resources, ClassLoader classLoader,
			MetadataReaderFactory metadataReaderFactory, @Nullable TypeFilter typeFilter,
			Predicate<Class<?>> predicate) {
		if (CollectionUtils.isEmpty(resources)) {
			return;
		}

		for (Resource resource : resources) {
			try {
				Class<?> clazz = resolve(resource, classLoader, metadataReaderFactory, typeFilter);
				if (clazz == null) {
					continue;
				}

				if (!predicate.test(clazz)) {
					break;
				}
			} catch (IOException e) {
			}
		}
		return;
	}

	public Class<?> resolve(Resource resource, ClassLoader classLoader, MetadataReaderFactory metadataReaderFactory,
			TypeFilter typeFilter) throws IOException {
		MetadataReader reader = null;
		try {
			reader = metadataReaderFactory.getMetadataReader(resource);
		} catch (NoClassDefFoundError e) {
		}

		if (reader == null) {
			return null;
		}

		AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
		if (annotationMetadata.hasAnnotation(Ignore.class.getName()) || !JavaVersion.isSupported(annotationMetadata)) {
			return null;
		}

		if (typeFilter != null && !typeFilter.match(reader, metadataReaderFactory)) {
			return null;
		}

		String name = reader.getClassMetadata().getClassName();
		Class<?> clazz = ClassUtils.getClass(name, classLoader);
		if (clazz == null) {
			return null;
		}
		return clazz;
	}
}
