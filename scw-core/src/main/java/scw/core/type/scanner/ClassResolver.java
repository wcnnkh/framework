package scw.core.type.scanner;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import scw.core.type.AnnotationMetadata;
import scw.core.type.classreading.MetadataReader;
import scw.core.type.classreading.MetadataReaderFactory;
import scw.core.type.classreading.SimpleMetadataReaderFactory;
import scw.core.type.filter.TypeFilter;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.lang.Ignore;
import scw.util.Accept;
import scw.util.DefaultClassLoaderProvider;
import scw.util.XUtils;

public class ClassResolver implements TypeFilter, Accept<Class<?>> {
	private MetadataReaderFactory metadataReaderFactory;

	public void setMetadataReaderFactory(
			MetadataReaderFactory metadataReaderFactory) {
		this.metadataReaderFactory = metadataReaderFactory;
	}

	protected MetadataReaderFactory getMetadataReaderFactory(
			ResourceLoader resourceLoader, ClassLoader classLoader) {
		if (metadataReaderFactory != null) {
			return metadataReaderFactory;
		}
		return new SimpleMetadataReaderFactory(resourceLoader, new DefaultClassLoaderProvider(classLoader));
	}

	public boolean accept(Class<?> clazz) {
		return Modifier.isPublic(clazz.getModifiers()) && XUtils.isAvailable(clazz);
	}

	public boolean match(MetadataReader metadataReader,
			MetadataReaderFactory metadataReaderFactory) throws IOException {
		if(metadataReader.getClassMetadata().isAnnotation()){
			return false;
		}
		
		AnnotationMetadata annotationMetadata = metadataReader
				.getAnnotationMetadata();
		if (annotationMetadata.hasAnnotation(Deprecated.class.getName())
				|| annotationMetadata.hasAnnotation(Ignore.class.getName())) {
			return false;
		}
		return true;
	}

	public final Set<Class<?>> resolve(Resource[] resources, ClassLoader classLoader,
			MetadataReaderFactory metadataReaderFactory, TypeFilter typeFilter) {
		if (ArrayUtils.isEmpty(resources)) {
			return Collections.emptySet();
		}

		return resolve(Arrays.asList(resources), classLoader,
				metadataReaderFactory, typeFilter);
	}

	public final Set<Class<?>> resolve(Resource[] resources, ClassLoader classLoader,
			ResourceLoader resourceLoader, TypeFilter typeFilter) {
		if (ArrayUtils.isEmpty(resources)) {
			return Collections.emptySet();
		}
		
		return resolve(resources, classLoader,
				getMetadataReaderFactory(resourceLoader, classLoader), typeFilter);
	}

	public final Set<Class<?>> resolve(Collection<Resource> resources, ClassLoader classLoader,
			ResourceLoader resourceLoader, TypeFilter typeFilter) throws IOException {
		if (CollectionUtils.isEmpty(resources)) {
			return Collections.emptySet();
		}
		
		return resolve(resources, classLoader,
				getMetadataReaderFactory(resourceLoader, classLoader), typeFilter);
	}

	public final Set<Class<?>> resolve(Collection<Resource> resources, ClassLoader classLoader,
			MetadataReaderFactory metadataReaderFactory, TypeFilter typeFilter) {
		if (CollectionUtils.isEmpty(resources)) {
			return Collections.emptySet();
		}

		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		for (Resource resource : resources) {
			try {
				Class<?> clazz = resolve(resource, classLoader,
						metadataReaderFactory, typeFilter);
				if (clazz != null) {
					classes.add(clazz);
				}
			} catch (IOException e) {
			}
		}
		return classes;
	}

	public Class<?> resolve(Resource resource,
			ClassLoader classLoader, MetadataReaderFactory metadataReaderFactory, TypeFilter typeFilter)
			throws IOException {
		MetadataReader reader = null;
		try {
			reader = metadataReaderFactory.getMetadataReader(resource);
		} catch (NoClassDefFoundError e) {
		}

		if (reader == null) {
			return null;
		}

		if (!match(reader, metadataReaderFactory)) {
			return null;
		}
		
		if(typeFilter != null && !typeFilter.match(reader, metadataReaderFactory)){
			return null;
		}

		String name = reader.getClassMetadata().getClassName();
		Class<?> clazz = ClassUtils.getClass(name, classLoader);
		if (clazz == null) {
			return null;
		}

		if (!accept(clazz)) {
			return null;
		}
		return clazz;
	}
}
