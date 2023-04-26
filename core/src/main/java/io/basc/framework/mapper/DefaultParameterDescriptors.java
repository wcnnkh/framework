package io.basc.framework.mapper;

import java.lang.reflect.AnnotatedElement;

import io.basc.framework.core.annotation.AnnotatedElementWrapper;
import io.basc.framework.util.Elements;

public class DefaultParameterDescriptors<T> extends AnnotatedElementWrapper<AnnotatedElement>
		implements ParameterDescriptors {
	private final T source;
	private final Class<?> declaringClass;
	private final Elements<ParameterDescriptor> elements;

	public DefaultParameterDescriptors(T source, Class<?> declaringClass, AnnotatedElement annotatedElement,
			Elements<ParameterDescriptor> elements) {
		super(annotatedElement);
		this.source = source;
		this.declaringClass = declaringClass;
		this.elements = elements;
	}

	public T getSource() {
		return source;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	@Override
	public Elements<ParameterDescriptor> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		return String.valueOf(source);
	}
}
