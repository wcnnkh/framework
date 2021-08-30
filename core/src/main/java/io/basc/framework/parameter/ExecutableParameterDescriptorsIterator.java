package io.basc.framework.parameter;

import io.basc.framework.reflect.ReflectionUtils;
import io.basc.framework.util.AbstractIterator;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

public class ExecutableParameterDescriptorsIterator extends AbstractIterator<ParameterDescriptors> {
	private final Iterator<? extends Executable> iterator;
	private final Class<?> targetClass;
	private final ParameterNameDiscoverer parameterNameDiscoverer;

	public ExecutableParameterDescriptorsIterator(ParameterNameDiscoverer parameterNameDiscoverer, Class<?> targetClass,
			Iterator<? extends Executable> iterator) {
		this.iterator = iterator;
		this.targetClass = targetClass;
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	public ExecutableParameterDescriptorsIterator(Class<?> clazz) {
		this(ParameterUtils.getParameterNameDiscoverer(), clazz,
				ReflectionUtils.getConstructorOrderList(clazz).iterator());
	}

	/**
	 * @param targetClass
	 * @param method
	 * @param polymorphic 是否将多态的方法也包含在内
	 */
	public ExecutableParameterDescriptorsIterator(Class<?> targetClass, final Method method, boolean polymorphic) {
		this(ParameterUtils.getParameterNameDiscoverer(), targetClass,
				polymorphic ? ReflectionUtils.getMethodOrderList(targetClass, method).iterator()
						: Arrays.asList(method).iterator());
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public ParameterDescriptors next() {
		Executable executable = iterator.next();
		return new ExecutableParameterDescriptors(parameterNameDiscoverer, targetClass, executable);
	}
}
