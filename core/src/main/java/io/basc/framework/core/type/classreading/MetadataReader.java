package io.basc.framework.core.type.classreading;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.io.Resource;

/**
 * Simple facade for accessing class metadata, as read by an ASM
 * {@link io.basc.framework.asm.ClassReader}.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/classreading/MetadataReader.java
 */
public interface MetadataReader {

	/**
	 * Return the resource reference for the class file.
	 */
	Resource getResource();

	/**
	 * Read basic class metadata for the underlying class.
	 */
	ClassMetadata getClassMetadata();

	/**
	 * Read full annotation metadata for the underlying class, including metadata
	 * for annotated methods.
	 */
	AnnotationMetadata getAnnotationMetadata();

}
