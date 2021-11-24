package io.basc.framework.core.type.scanner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.classreading.SimpleMetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.lang.Ignore;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.JavaVersion;

public class ClassResolver {
	private MetadataReaderFactory metadataReaderFactory;

	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
		this.metadataReaderFactory = metadataReaderFactory;
	}

	protected MetadataReaderFactory getMetadataReaderFactory(ResourceLoader resourceLoader, ClassLoader classLoader) {
		if (metadataReaderFactory != null) {
			return metadataReaderFactory;
		}
		return new SimpleMetadataReaderFactory(resourceLoader, new DefaultClassLoaderProvider(classLoader));
	}

	public final Set<Class<?>> resolve(Resource[] resources, ClassLoader classLoader,
			MetadataReaderFactory metadataReaderFactory, TypeFilter typeFilter) {
		if (ArrayUtils.isEmpty(resources)) {
			return Collections.emptySet();
		}

		return resolve(Arrays.asList(resources), classLoader, metadataReaderFactory, typeFilter);
	}

	public final Set<Class<?>> resolve(Resource[] resources, ClassLoader classLoader, ResourceLoader resourceLoader,
			TypeFilter typeFilter) {
		if (ArrayUtils.isEmpty(resources)) {
			return Collections.emptySet();
		}

		return resolve(resources, classLoader, getMetadataReaderFactory(resourceLoader, classLoader), typeFilter);
	}

	public final Set<Class<?>> resolve(Collection<Resource> resources, ClassLoader classLoader,
			ResourceLoader resourceLoader, TypeFilter typeFilter) throws IOException {
		if (CollectionUtils.isEmpty(resources)) {
			return Collections.emptySet();
		}

		return resolve(resources, classLoader, getMetadataReaderFactory(resourceLoader, classLoader), typeFilter);
	}

	public final Set<Class<?>> resolve(Collection<Resource> resources, ClassLoader classLoader,
			MetadataReaderFactory metadataReaderFactory, TypeFilter typeFilter) {
		if (CollectionUtils.isEmpty(resources)) {
			return Collections.emptySet();
		}

		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		for (Resource resource : resources) {
			try {
				Class<?> clazz = resolve(resource, classLoader, metadataReaderFactory, typeFilter);
				if (clazz != null) {
					classes.add(clazz);
				}
			} catch (IOException e) {
			}
		}
		return classes;
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
