package io.basc.framework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.reflect.ReflectionUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class BeanUtils {
	public static interface Filter {
		boolean doFilter(Object source, PropertyDescriptor sourcePropertyDescriptor, Object target,
				PropertyDescriptor targetPropertyDescriptor, Mapping mapping) throws Throwable;
	}

	@RequiredArgsConstructor
	public static class FilterableMapping implements Mapping {
		@NonNull
		private final List<Filter> filters;
		private final Mapping mapping;

		@Override
		public boolean doMapping(Object source, PropertyDescriptor sourcePropertyDescriptor, Object target,
				PropertyDescriptor targetPropertyDescriptor) throws Throwable {
			MappingChain chain = new MappingChain(filters.iterator(), mapping);
			return chain.doMapping(source, sourcePropertyDescriptor, target, targetPropertyDescriptor);
		}

	}

	public static interface Mapping {
		boolean doMapping(Object source, @NonNull PropertyDescriptor sourcePropertyDescriptor, Object target,
				@NonNull PropertyDescriptor targetPropertyDescriptor) throws Throwable;
	}

	@RequiredArgsConstructor
	public static class MappingChain implements Mapping {
		@NonNull
		private final Iterator<? extends Filter> iterator;
		private final Mapping nextChain;

		@Override
		public boolean doMapping(Object source, PropertyDescriptor sourcePropertyDescriptor, Object target,
				PropertyDescriptor targetPropertyDescriptor) throws Throwable {
			if (iterator.hasNext()) {
				return iterator.next().doFilter(source, sourcePropertyDescriptor, target, targetPropertyDescriptor,
						this);
			} else if (nextChain != null) {
				return nextChain.doMapping(source, sourcePropertyDescriptor, target, targetPropertyDescriptor);
			}
			return false;
		}
	}

	private static final BeanInfoFactories BEAN_INFO_FACTORIES = new BeanInfoFactories();

	private static final BeanInfoRegistry BEAN_INFO_REGISTRY = new BeanInfoRegistry();

	/**
	 * 默认的映射
	 */
	public static final Mapping COPY = (source, sourcePropertyDescriptor, target, targetProperyDescriptor) -> {
		Method writeMethod = targetProperyDescriptor.getWriteMethod();
		if (writeMethod == null) {
			return false;
		}

		Method readMethod = sourcePropertyDescriptor.getReadMethod();
		if (readMethod == null) {
			return false;
		}

		ResolvableType sourceResolvableType = ResolvableType.forMethodReturnType(readMethod);
		ResolvableType targetResolvableType = ResolvableType.forMethodParameter(writeMethod, 0);

		// Ignore generic types in assignable check if either ResolvableType has
		// unresolvable generics.
		boolean isAssignable = (sourceResolvableType.hasUnresolvableGenerics()
				|| targetResolvableType.hasUnresolvableGenerics()
						? ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())
						: targetResolvableType.isAssignableFrom(sourceResolvableType));
		if (isAssignable) {
			if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
				readMethod.setAccessible(true);
			}
			Object value = readMethod.invoke(source);
			if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
				writeMethod.setAccessible(true);
			}
			writeMethod.invoke(target, value);
			return true;
		}
		return false;
	};

	/**
	 * 忽略空
	 */
	public static final Filter IGNORE_NULL_FILTER = (source, sourcePropertyDescriptor, target, targetProperyDescriptor,
			mapping) -> {
		Method readMethod = sourcePropertyDescriptor.getReadMethod();
		if (readMethod == null) {
			return false;
		}

		Object value = ReflectionUtils.invoke(readMethod, source);
		if (value == null) {
			return false;
		}
		return mapping.doMapping(source, sourcePropertyDescriptor, target, targetProperyDescriptor);
	};

	static {
		BEAN_INFO_FACTORIES.doNativeConfigure();
		BEAN_INFO_REGISTRY.setBeanInfoFactory(BEAN_INFO_FACTORIES);
	}

	public static void copyProperties(@NonNull Object source, @NonNull Object target, @NonNull Filter... filters) {
		FilterableMapping mapping = new FilterableMapping(Arrays.asList(filters), COPY);
		doMapping(source, source.getClass(), target, target.getClass(), mapping);
	}

	public static <S, T> void doMapping(S source, @NonNull Class<? extends S> sourceClass, T target,
			@NonNull Class<? extends T> targetClass, @NonNull Mapping mapping) {
		BeanPropertyDescriptors sourcePropertyDescriptors = getPropertyDescriptors(sourceClass);
		BeanPropertyDescriptors targetPropertyDescriptors = getPropertyDescriptors(targetClass);
		for (String name : targetPropertyDescriptors.keys()) {
			List<PropertyDescriptor> sourceList = sourcePropertyDescriptors.getValues(name)
					.collect(Collectors.toList());
			for (PropertyDescriptor targetPropertyDescriptor : targetPropertyDescriptors.getValues(name)) {
				Iterator<PropertyDescriptor> iterator = sourceList.iterator();
				while (iterator.hasNext()) {
					PropertyDescriptor sourcePropertyDescriptor = iterator.next();
					try {
						if (mapping.doMapping(source, sourcePropertyDescriptor, target, targetPropertyDescriptor)) {
							// 映射成功
							iterator.remove();
						}
					} catch (Throwable e) {
						throw new FatalBeanException(
								"Could not copy property '" + targetPropertyDescriptor + "' from source to target", e);
					}
				}
			}
		}
	}

	public static CachedBeanInfo getBeanInfo(Class<?> beanClass) {
		return BEAN_INFO_REGISTRY.getBeanInfo(beanClass);
	}

	public static BeanInfoRegistry getBeanInfoRegistry() {
		return BEAN_INFO_REGISTRY;
	}

	public static BeanPropertyDescriptors getPropertyDescriptors(Class<?> beanClass) {
		CachedBeanInfo beanInfo = getBeanInfo(beanClass);
		return beanInfo.getSharedPropertyDescriptors();
	}

	public static Elements<PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass, String name) {
		BeanPropertyDescriptors propertyDescriptors = getPropertyDescriptors(beanClass);
		return propertyDescriptors.getValues(name);
	}

	public static boolean isCacheSafe(@NonNull Class<?> clazz, ClassLoader classLoader) {
		try {
			ClassLoader target = clazz.getClassLoader();
			// Common cases
			if (target == classLoader || target == null) {
				return true;
			}
			if (classLoader == null) {
				return false;
			}
			// Check for match in ancestors -> positive
			ClassLoader current = classLoader;
			while (current != null) {
				current = current.getParent();
				if (current == target) {
					return true;
				}
			}
			// Check for match in children -> negative
			while (target != null) {
				target = target.getParent();
				if (target == classLoader) {
					return false;
				}
			}
		} catch (SecurityException ex) {
			// Fall through to loadable check below
		}

		// Fallback for ClassLoaders without parent/child relationship:
		// safe if same Class can be loaded from given ClassLoader
		return (classLoader != null && isLoadable(clazz, classLoader));
	}

	/**
	 * Check whether the given class is loadable in the given ClassLoader.
	 * 
	 * @param clazz       the class to check (typically an interface)
	 * @param classLoader the ClassLoader to check against
	 */
	private static boolean isLoadable(Class<?> clazz, ClassLoader classLoader) {
		try {
			return (clazz == classLoader.loadClass(clazz.getName()));
			// Else: different class with same name found
		} catch (ClassNotFoundException ex) {
			// No corresponding class found at all
			return false;
		}
	}
}
