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

	/**
	 * Get the name of the underlying method.
	 */
	String getMethodName();

	/**
	 * Get the fully-qualified name of the class that declares the underlying
	 * method.
	 */
	String getDeclaringClassName();

	/**
	 * Get the fully-qualified name of the underlying method's declared return type.
	 * 
	 */
	String getReturnTypeName();

	/**
	 * Determine whether the underlying method is effectively abstract: i.e. marked
	 * as abstract in a class or declared as a regular, non-default method in an
	 * interface.
	 * 
	 */
	boolean isAbstract();

	/**
	 * Determine whether the underlying method is declared as 'static'.
	 */
	boolean isStatic();

	/**
	 * Determine whether the underlying method is marked as 'final'.
	 */
	boolean isFinal();

	/**
	 * Determine whether the underlying method is overridable, i.e. not marked as
	 * static, final, or private.
	 */
	boolean isOverridable();

}
