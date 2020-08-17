package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import scw.core.reflect.ReflectionUtils;

public class ConstructorParameterDescriptorsIterator extends scw.util.Iterator<ParameterDescriptors> {
	private final Iterator<Constructor<?>> iterator;
	private final Class<?> targetClass;

	public ConstructorParameterDescriptorsIterator(Class<?> targetClass, Iterator<Constructor<?>> iterator) {
		this.iterator = iterator;
		this.targetClass = targetClass;
	}

	public ConstructorParameterDescriptorsIterator(Class<?> clazz) {
		this(clazz, ReflectionUtils.getConstructorOrderList(clazz).iterator());
	}
	
	public boolean hasNext() {
		return iterator.hasNext();
	}

	public ParameterDescriptors next() {
		Constructor<?> constructor = iterator.next();
		return new ConstructorParameterDescriptors(targetClass, constructor);
	}

}
