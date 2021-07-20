package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import scw.core.reflect.ReflectionUtils;

public class ConstructorParameterDescriptorsIterator extends scw.util.AbstractIterator<ParameterDescriptors> {
	private final Iterator<Constructor<?>> iterator;
	private final Class<?> targetClass;
	private final ParameterNameDiscoverer parameterNameDiscoverer;

	public ConstructorParameterDescriptorsIterator(ParameterNameDiscoverer parameterNameDiscoverer,
			Class<?> targetClass, Iterator<Constructor<?>> iterator) {
		this.iterator = iterator;
		this.targetClass = targetClass;
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	public ConstructorParameterDescriptorsIterator(Class<?> clazz) {
		this(ParameterUtils.getParameterNameDiscoverer(), clazz,
				ReflectionUtils.getConstructorOrderList(clazz).iterator());
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public ParameterDescriptors next() {
		Constructor<?> constructor = iterator.next();
		return new ConstructorParameterDescriptors(parameterNameDiscoverer, targetClass, constructor);
	}

}
