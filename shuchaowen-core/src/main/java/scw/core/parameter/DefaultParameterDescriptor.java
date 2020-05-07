package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.core.annotation.AnnotatedElementUtils;

public class DefaultParameterDescriptor implements ParameterDescriptor {
	private final String name;
	private final AnnotatedElement annotatedElement;
	private final Class<?> type;
	private final Type genericType;

	public DefaultParameterDescriptor(String name, Class<?> type, Type genericType) {
		this(name, AnnotatedElementUtils.EMPTY_ANNOTATED_ELEMENT, type, genericType);
	}

	public DefaultParameterDescriptor(String name, Annotation[] annotations, Class<?> type, Type genericType) {
		this(name, AnnotatedElementUtils.forAnnotations(annotations), type, genericType);
	}

	public DefaultParameterDescriptor(String name, AnnotatedElement annotatedElement, Class<?> type, Type genericType) {
		this.name = name;
		this.annotatedElement = annotatedElement;
		this.type = type;
		this.genericType = genericType;
	}
	
	/**
	 * 重新取个名字
	 * @param name
	 * @param parameterDescriptor
	 */
	public DefaultParameterDescriptor(String name, ParameterDescriptor parameterDescriptor) {
		this(name, parameterDescriptor.getAnnotatedElement(), parameterDescriptor.getType(), parameterDescriptor.getGenericType());
	}

	public String getName() {
		return name;
	}

	public AnnotatedElement getAnnotatedElement() {
		return annotatedElement;
	}

	public Class<?> getType() {
		return type;
	}

	public Type getGenericType() {
		return genericType;
	}
}
