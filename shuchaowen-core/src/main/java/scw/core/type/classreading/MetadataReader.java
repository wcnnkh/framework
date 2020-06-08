package scw.core.type.classreading;

import scw.core.type.AnnotationMetadata;
import scw.core.type.ClassMetadata;
import scw.io.Resource;

/**
 * Simple facade for accessing class metadata,
 * as read by an ASM {@link scw.asm.ClassReader}.
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
