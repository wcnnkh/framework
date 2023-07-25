package io.basc.framework.mapper.support;

import java.lang.reflect.AnnotatedElement;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.util.element.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultParameterDescriptors<T> implements ParameterDescriptors {
	private final Class<?> sourceClass;
	private final T source;
	private final AnnotatedElement sourceAnnotatedElement;
	private final Elements<ParameterDescriptor> elements;

	@Override
	public String toString() {
		return String.valueOf(source);
	}
}
