package io.basc.framework.mapper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import io.basc.framework.core.ParameterNameDiscoverer;
import io.basc.framework.core.annotation.Order;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.comparator.CompareUtils;

public class ExecutableParameterDescriptorsIterator implements Iterator<ParameterDescriptors> {
	private static final Comparator<Constructor<?>> CONSTRUCTOR_COMPARATOR = new Comparator<Constructor<?>>() {

		public int compare(Constructor<?> o1, Constructor<?> o2) {
			Deprecated d1 = o1.getAnnotation(Deprecated.class);
			Deprecated d2 = o2.getAnnotation(Deprecated.class);

			// 先比较作用域 public
			int v1 = o1.getModifiers();
			int v2 = o2.getModifiers();
			if (!(d1 != null && d2 != null)) {
				if (d1 != null) {
					v1 = Integer.MAX_VALUE;
				}

				if (d2 != null) {
					v2 = Integer.MAX_VALUE;
				}
			}

			if (v1 == v2) {
				return CompareUtils.compare(o1.getParameterTypes().length, o2.getParameterTypes().length, true);
			}
			return CompareUtils.compare(v1, v2, false);
		}
	};

	private static final Comparator<Method> METHOD_COMPARATOR = new Comparator<Method>() {

		public int compare(Method o1, Method o2) {
			Deprecated d1 = o1.getAnnotation(Deprecated.class);
			Deprecated d2 = o2.getAnnotation(Deprecated.class);

			// 先比较作用域 public
			int v1 = o1.getModifiers();
			int v2 = o2.getModifiers();
			if (!(d1 != null && d2 != null)) {
				if (d1 != null) {
					v1 = Integer.MAX_VALUE;
				}

				if (d2 != null) {
					v2 = Integer.MAX_VALUE;
				}
			}

			if (v1 == v2) {
				return CompareUtils.compare(o1.getParameterTypes().length, o2.getParameterTypes().length, true);
			}
			return CompareUtils.compare(v1, v2, false);
		}
	};

	public static <E extends AnnotatedElement> int compare(E o1, E o2, Comparator<E> comparator) {
		Order auto1 = o1.getAnnotation(Order.class);
		Order auto2 = o2.getAnnotation(Order.class);
		if (auto1 != null && auto2 != null) {
			if (auto1.value() == auto2.value()) {
				return comparator.compare(o1, o2);
			}
			return CompareUtils.compare(auto1.value(), auto2.value(), true);
		} else if (auto1 == null && auto2 == null) {
			return comparator.compare(o1, o2);
		} else {
			return auto1 != null ? 1 : 0;
		}
	}

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
		this(ParameterUtils.getParameterNameDiscoverer(), clazz, ReflectionUtils.getDeclaredConstructors(clazz)
				.sorted((o1, o2) -> compare(o1, o2, CONSTRUCTOR_COMPARATOR)).iterator());
	}

	public ExecutableParameterDescriptorsIterator(Class<?> targetClass, final Method method, boolean polymorphic) {
		this(ParameterUtils.getParameterNameDiscoverer(), targetClass,
				polymorphic
						? ReflectionUtils.getDeclaredMethods(targetClass).getElements()
								.sorted((o1, o2) -> compare(o1, o2, METHOD_COMPARATOR)).iterator()
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
