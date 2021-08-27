package io.basc.framework.core.type.classreading;

import io.basc.framework.core.utils.ClassUtils;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.DefaultClassLoaderProvider;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Simple implementation of the {@link MetadataReaderFactory} interface,
 * creating a new ASM {@link scw.asm.ClassReader} for every request.
 */
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {
	private final ResourceLoader resourceLoader;
	private final ClassLoaderProvider classLoaderProvider;

	/**
	 * Create a new SimpleMetadataReaderFactory for the default class loader.
	 */
	public SimpleMetadataReaderFactory(){
		this(new DefaultResourceLoader());
	}
	
	public SimpleMetadataReaderFactory(ClassLoaderProvider classLoaderProvider) {
		this(new DefaultResourceLoader(classLoaderProvider), classLoaderProvider);
	}
	
	public SimpleMetadataReaderFactory(ResourceLoader resourceLoader) {
		this(resourceLoader, resourceLoader);
	}

	/**
	 * Create a new SimpleMetadataReaderFactory for the given resource loader.
	 */
	public SimpleMetadataReaderFactory(ResourceLoader resourceLoader, ClassLoaderProvider classLoaderProvider) {
		this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
		this.classLoaderProvider = classLoaderProvider;
	}

	/**
	 * Create a new SimpleMetadataReaderFactory for the given class loader.
	 * @param classLoader the ClassLoader to use
	 */
	public SimpleMetadataReaderFactory(ClassLoader classLoader) {
		this(new DefaultClassLoaderProvider(classLoader));
	}


	/**
	 * Return the ResourceLoader that this MetadataReaderFactory has been
	 * constructed with.
	 */
	public final ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public MetadataReader getMetadataReader(String className) throws IOException {
		ResourceLoader resourceLoader = getResourceLoader();
		try {
			String resourcePath = ResourceLoader.CLASSPATH_URL_PREFIX +
					ClassUtils.convertClassNameToResourcePath(className) + ClassUtils.CLASS_FILE_SUFFIX;
			Resource resource = resourceLoader.getResource(resourcePath);
			return getMetadataReader(resource);
		}
		catch (FileNotFoundException ex) {
			// Maybe an inner class name using the dot name syntax? Need to use the dollar syntax here...
			// ClassUtils.forName has an equivalent check for resolution into Class references later on.
			int lastDotIndex = className.lastIndexOf('.');
			if (lastDotIndex != -1) {
				String innerClassName =
						className.substring(0, lastDotIndex) + '$' + className.substring(lastDotIndex + 1);
				String innerClassResourcePath = ResourceLoader.CLASSPATH_URL_PREFIX +
						ClassUtils.convertClassNameToResourcePath(innerClassName) + ClassUtils.CLASS_FILE_SUFFIX;
				Resource innerClassResource = resourceLoader.getResource(innerClassResourcePath);
				if (innerClassResource.exists()) {
					return getMetadataReader(innerClassResource);
				}
			}
			throw ex;
		}
	}

	public MetadataReader getMetadataReader(Resource resource) throws IOException {
		return new SimpleMetadataReader(resource, ClassUtils.getClassLoader(getClassLoaderProvider()));
	}

}
