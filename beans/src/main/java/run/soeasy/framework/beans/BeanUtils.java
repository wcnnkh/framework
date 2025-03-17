package run.soeasy.framework.beans;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class BeanUtils {
	public static interface Filter {
		boolean doFilter(Object source, @NonNull BeanPropertyDescriptor sourcePropertyDescriptor, Object target,
				@NonNull BeanPropertyDescriptor targetPropertyDescriptor, @NonNull String name,
				@NonNull Mapping mapping) throws Throwable;
	}

	@RequiredArgsConstructor
	public static class FilterableMapping implements Mapping {
		@NonNull
		private final List<Filter> filters;
		private final Mapping mapping;

		@Override
		public boolean doMapping(Object source, @NonNull BeanPropertyDescriptor sourcePropertyDescriptor, Object target,
				@NonNull BeanPropertyDescriptor targetPropertyDescriptor, @NonNull String name) throws Throwable {
			MappingChain chain = new MappingChain(filters.iterator(), mapping);
			return chain.doMapping(source, sourcePropertyDescriptor, target, targetPropertyDescriptor, name);
		}

	}

	public static interface Mapping {
		boolean doMapping(Object source, @NonNull BeanPropertyDescriptor sourcePropertyDescriptor, Object target,
				@NonNull BeanPropertyDescriptor targetPropertyDescriptor, @NonNull String name) throws Throwable;
	}

	@RequiredArgsConstructor
	public static class MappingChain implements Mapping {
		@NonNull
		private final Iterator<? extends Filter> iterator;
		private final Mapping nextChain;

		@Override
		public boolean doMapping(Object source, @NonNull BeanPropertyDescriptor sourcePropertyDescriptor, Object target,
				@NonNull BeanPropertyDescriptor targetPropertyDescriptor, @NonNull String name) throws Throwable {
			if (iterator.hasNext()) {
				return iterator.next().doFilter(source, sourcePropertyDescriptor, target, targetPropertyDescriptor,
						name, this);
			} else if (nextChain != null) {
				return nextChain.doMapping(source, sourcePropertyDescriptor, target, targetPropertyDescriptor, name);
			}
			return false;
		}
	}

	private static final BeanMappingRegistry BEAN_MAPPING_REGISTRY = new BeanMappingRegistry();

	/**
	 * 默认的映射
	 */
	public static final Mapping COPY_PROPERTIES = (source, sourcePropertyDescriptor, target, targetProperyDescriptor,
			name) -> {
		if (!(sourcePropertyDescriptor.hasReadMethod() && sourcePropertyDescriptor.isReadable()
				&& targetProperyDescriptor.hasWriteMethod() && targetProperyDescriptor.isWritable())) {
			return false;
		}

		if (!sourcePropertyDescriptor.getTypeDescriptor()
				.isAssignableTo(targetProperyDescriptor.getRequiredTypeDescriptor())) {
			return false;
		}
		Object value = sourcePropertyDescriptor.readFrom(source);
		targetProperyDescriptor.writeTo(target, value);
		return true;
	};

	/**
	 * 忽略空
	 */
	public static final Filter IGNORE_NULL_FILTER = (source, sourcePropertyDescriptor, target, targetProperyDescriptor,
			name, mapping) -> {
		if (!sourcePropertyDescriptor.isReadable()) {
			return false;
		}

		Object value = sourcePropertyDescriptor.readFrom(source);
		if (value == null) {
			return false;
		}
		return mapping.doMapping(source, sourcePropertyDescriptor, target, targetProperyDescriptor, name);
	};

	static {
		BeanInfoFactories beanInfoFactories = new BeanInfoFactories();
		beanInfoFactories.doNativeConfigure();
		BEAN_MAPPING_REGISTRY.setMappingDescriptorFactory(beanInfoFactories);
	}

	public static void copyProperties(@NonNull Object source, @NonNull Object target, @NonNull Filter... filters) {
		FilterableMapping mapping = new FilterableMapping(Arrays.asList(filters), COPY_PROPERTIES);
		doMapping(source, source.getClass(), target, target.getClass(), mapping);
	}

	public static <S, T> void doMapping(S source, @NonNull Class<? extends S> sourceClass, T target,
			@NonNull Class<? extends T> targetClass, @NonNull Mapping mapping) {
		BeanMapping sourceMapping = getMapping(sourceClass);
		BeanMapping targetMapping = getMapping(targetClass);
		for (String name : sourceMapping.keys()) {
			List<BeanPropertyDescriptor> sourceList = sourceMapping.getValues(name).collect(Collectors.toList());
			for (BeanPropertyDescriptor targetPropertyDescriptor : targetMapping.getValues(name)) {
				Iterator<BeanPropertyDescriptor> iterator = sourceList.iterator();
				while (iterator.hasNext()) {
					BeanPropertyDescriptor sourcePropertyDescriptor = iterator.next();
					try {
						if (mapping.doMapping(source, sourcePropertyDescriptor, target, targetPropertyDescriptor,
								name)) {
							// 映射成功
							iterator.remove();
						}
					} catch (Throwable e) {
						throw new FatalBeanException("Could not copy property '" + name + "' from source to target", e);
					}
				}
			}
		}
	}

	public static BeanMappingRegistry getBeanMappingRegistry() {
		return BEAN_MAPPING_REGISTRY;
	}

	public static BeanMapping getMapping(Class<?> beanClass) {
		return BEAN_MAPPING_REGISTRY.getMappingDescriptor(TypeDescriptor.valueOf(beanClass));
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
