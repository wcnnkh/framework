package io.basc.framework.parameter;

import io.basc.framework.annotation.AnnotatedElementUtils;
import io.basc.framework.annotation.MultiAnnotatedElement;
import io.basc.framework.util.AbstractIterator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Iterator;

public class DefaultParameterDescriptors<T> implements ParameterDescriptors {
	private final T source;
	private final Class<?> declaringClass;
	private final ParameterDescriptor[] parameterDescriptors;

	public DefaultParameterDescriptors(T source, Class<?> declaringClass, ParameterDescriptor[] parameterDescriptors) {
		this.source = source;
		this.declaringClass = declaringClass;
		this.parameterDescriptors = parameterDescriptors;
	}

	public DefaultParameterDescriptors(Class<?> declaringClass, T source, String[] names, Annotation[][] annotations,
			AnnotatedElement commonAnnotations, Type[] genericTypes, Class<?>[] types) {
		this.source = source;
		this.declaringClass = declaringClass;
		parameterDescriptors = new ParameterDescriptor[names.length];
		for (int index = 0; index < names.length; index++) {
			AnnotatedElement annotatedElement = MultiAnnotatedElement
					.forAnnotatedElements(AnnotatedElementUtils.forAnnotations(annotations[index]), commonAnnotations);
			parameterDescriptors[index] = new DefaultParameterDescriptor(names[index], annotatedElement, types[index],
					genericTypes[index]);
		}
	}

	@Override
	public ParameterDescriptor[] toArray() {
		return parameterDescriptors.clone();
	}

	public T getSource() {
		return source;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public Iterator<ParameterDescriptor> iterator() {
		return new InternalIterator();
	}

	public int size() {
		return parameterDescriptors == null ? 0 : parameterDescriptors.length;
	}

	private class InternalIterator extends AbstractIterator<ParameterDescriptor> {
		private int index = 0;

		public boolean hasNext() {
			return parameterDescriptors != null && index < parameterDescriptors.length;
		}

		public ParameterDescriptor next() {
			return getParameterDescriptor(index++);
		}
	}

	public Class<?>[] getTypes() {
		Class<?>[] types = new Class<?>[size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = parameterDescriptors[i].getType();
		}
		return types;
	}

	public ParameterDescriptor getParameterDescriptor(int index) {
		if (index >= size()) {
			throw new IndexOutOfBoundsException(index + "");
		}
		return parameterDescriptors[index];
	}

	public ParameterDescriptor getParameterDescriptor(String name) {
		for (ParameterDescriptor parameterDescriptor : this) {
			if (parameterDescriptor.getName().equals(name)) {
				return parameterDescriptor;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return String.valueOf(source);
	}
}
