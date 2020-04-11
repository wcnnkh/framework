package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.ParameterName;
import scw.core.utils.StringUtils;

public class DefaultParameterDescriptor implements ParameterDescriptor {
	private final String name;
	private final AnnotatedElement annotatedElement;
	private final Class<?> type;
	private final Type genericType;

	public DefaultParameterDescriptor(String name, Annotation[] annotations, Class<?> type,
			Type genericType) {
		this.name = name;
		this.annotatedElement = AnnotatedElementUtils.forAnnotations(annotations);
		this.type = type;
		this.genericType = genericType;
	}

	public String getName() {
		ParameterName parameterName = getAnnotatedElement().getAnnotation(ParameterName.class);
		if (parameterName != null
				&& StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
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
