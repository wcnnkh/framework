package scw.core.parameter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

import scw.core.reflect.ReflectionUtils;

public class MethodParameterDescriptorsIterator extends scw.util.AbstractIterator<ParameterDescriptors> {
	private final Iterator<Method> iterator;
	private final Class<?> targetClass;

	public MethodParameterDescriptorsIterator(Class<?> targetClass, Iterator<Method> iterator) {
		this.targetClass = targetClass;
		this.iterator = iterator;
	}

	/**
	 * @param targetClass
	 * @param method
	 * @param polymorphic 是否将多态的方法也包含在内
	 */
	public MethodParameterDescriptorsIterator(Class<?> targetClass, final Method method, boolean polymorphic) {
		this.targetClass = targetClass;
		if (polymorphic) {
			this.iterator = ReflectionUtils.getMethodOrderList(targetClass, method).iterator();
		} else {
			this.iterator = Arrays.asList(method).iterator();
		}
	}
	
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
	public ParameterDescriptors next() {
		Method method = iterator.next();
		return new MethodParameterDescriptors(targetClass, method);
	}
}
