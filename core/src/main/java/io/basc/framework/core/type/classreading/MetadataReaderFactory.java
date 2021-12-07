package io.basc.framework.core.type.classreading;

import java.io.IOException;

import io.basc.framework.io.Resource;

/**
 * Factory interface for {@link MetadataReader} instances. Allows for caching a
 * MetadataReader per original resource.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/classreading/MetadataReaderFactory.java
 * @see SimpleMetadataReaderFactory
 * @see CachingMetadataReaderFactory
 */
public interface MetadataReaderFactory {

	/**
	 * Obtain a MetadataReader for the given class name.
	 * 
	 * @param className the class name (to be resolved to a ".class" file)
	 * @return a holder for the ClassReader instance (never {@code null})
	 * @throws IOException in case of I/O failure
	 */
	MetadataReader getMetadataReader(String className) throws IOException;

	/**
	 * Obtain a MetadataReader for the given resource.
	 * 
	 * @param resource the resource (pointing to a ".class" file)
	 * @return a holder for the ClassReader instance (never {@code null})
	 * @throws IOException in case of I/O failure
	 */
	MetadataReader getMetadataReader(Resource resource) throws IOException;

}
