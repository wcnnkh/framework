package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.parameter.annotation.Named;
import scw.mapper.MapperUtils;

public class DefaultParameterDescriptor implements ParameterDescriptor {
	private final String name;
	private final AnnotatedElement annotatedElement;
	private final Class<?> type;
	private final Type genericType;

	public DefaultParameterDescriptor(String name, Class<?> type) {
		this(name, type, type);
	}

	public DefaultParameterDescriptor(String name, Class<?> type, Type genericType) {
		this(name, AnnotatedElementUtils.EMPTY_ANNOTATED_ELEMENT, type, genericType);
	}

	public DefaultParameterDescriptor(String name, Annotation[] annotations, Class<?> type, Type genericType) {
		this(name, AnnotatedElementUtils.forAnnotations(annotations), type, genericType);
	}

	public DefaultParameterDescriptor(String name, AnnotatedElement annotatedElement, Class<?> type, Type genericType) {
		this.annotatedElement = annotatedElement;
		if (annotatedElement == null) {
			this.name = name;
		} else {
			Named parameterName = annotatedElement.getAnnotation(Named.class);
			this.name = parameterName == null ? name : parameterName.value();
		}
		this.type = type;
		this.genericType = genericType;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public Type getGenericType() {
		return genericType;
	}

	public boolean isNullable() {
		return AnnotationUtils.isNullable(annotatedElement, false);
	}

	@Override
	public String toString() {
		return MapperUtils.getMapper().getFields(getClass()).getValueMap(this).toString();
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return this.annotatedElement.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getAnnotations() {
		return this.annotatedElement.getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return this.annotatedElement.getDeclaredAnnotations();
	}
}
