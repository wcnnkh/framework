package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.core.annotation.AnnotatedElementUtils;

public class DefaultParameterDescriptor extends AbstractParameterDescriptor {
	private final String name;
	private final AnnotatedElement annotatedElement;
	private final Class<?> type;
	private final Type genericType;

	public DefaultParameterDescriptor(String name, Annotation[] annotations, Class<?> type, Type genericType) {
		this.name = name;
		this.annotatedElement = AnnotatedElementUtils.forAnnotations(annotations);
		this.type = type;
		this.genericType = genericType;
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
