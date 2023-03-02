package io.basc.framework.core.type;

/**
 * Interface that defines abstract access to the annotations of a specific
 * method, in a form that does not require that method's class to be loaded yet.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/MethodMetadata.java
 * @see StandardMethodMetadata
 * @see AnnotationMetadata#getAnnotatedMethods
 * @see AnnotatedTypeMetadata
 */
public interface MethodMetadata extends AnnotatedTypeMetadata {

	String getMethodName();

	String getDeclaringClassName();

	String getReturnTypeName();

	boolean isAbstract();

	boolean isStatic();

	boolean isFinal();

	boolean isOverridable();

}
