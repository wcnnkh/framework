package io.basc.framework.type.classreading;

import io.basc.framework.io.Resource;
import io.basc.framework.type.AnnotationMetadata;
import io.basc.framework.type.ClassMetadata;

/**
 * Simple facade for accessing class metadata,
 * as read by an ASM {@link io.basc.framework.asm.ClassReader}.
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
	 * Read full annotation metadata for the underlying class,
	 * including metadata for annotated methods.
	 */
	AnnotationMetadata getAnnotationMetadata();

}
