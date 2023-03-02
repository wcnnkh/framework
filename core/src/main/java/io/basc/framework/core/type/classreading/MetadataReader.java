package io.basc.framework.core.type.classreading;

import org.objectweb.asm.ClassReader;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.io.Resource;

/**
 * Simple facade for accessing class metadata, as read by an ASM
 * {@link ClassReader}.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/classreading/MetadataReader.java
 */
public interface MetadataReader {

	Resource getResource();

	ClassMetadata getClassMetadata();

	AnnotationMetadata getAnnotationMetadata();
}
