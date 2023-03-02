package io.basc.framework.core.type;

import io.basc.framework.lang.Nullable;

/**
 * Interface that defines abstract metadata of a specific class, in a form that
 * does not require that class to be loaded yet.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/ClassMetadata.java
 * @see StandardClassMetadata
 * @see io.basc.framework.core.type.classreading.MetadataReader#getClassMetadata()
 * @see AnnotationMetadata
 */
public interface ClassMetadata {

	String getClassName();

	boolean isInterface();

	boolean isAnnotation();

	boolean isAbstract();

	default boolean isConcrete() {
		return !(isInterface() || isAbstract());
	}

	boolean isFinal();

	boolean isEnum();

	boolean isPublic();

	boolean isIndependent();

	default boolean hasEnclosingClass() {
		return (getEnclosingClassName() != null);
	}

	@Nullable
	String getEnclosingClassName();

	default boolean hasSuperClass() {
		return (getSuperClassName() != null);
	}

	@Nullable
	String getSuperClassName();

	String[] getInterfaceNames();

	String[] getMemberClassNames();

}
