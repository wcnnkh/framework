package io.basc.framework.factory;

import java.util.function.Supplier;

public interface InstanceFactory extends NoArgsInstanceFactory, DefinitionFactory {
	boolean isInstance(String name, Object... params);

	<T> T getInstance(String name, Object... params);

	default <T> Supplier<T> getInstanceSupplier(String name, Object... params) {
		return new Supplier<T>() {
			@Override
			public T get() {
				return getInstance(name, params);
			}
		};
	}

	boolean isInstance(Class<?> clazz, Object... params);

	<T> T getInstance(Class<T> clazz, Object... params);

	default <T> Supplier<T> getInstanceSupplier(Class<T> clazz, Object... params) {
		return new Supplier<T>() {
			@Override
			public T get() {
				return getInstance(clazz, params);
			}
		};
	}

	boolean isInstance(String name, Class<?>[] parameterTypes);

	<T> T getInstance(String name, Class<?>[] parameterTypes, Object[] params);

	default <T> Supplier<T> getInstanceSupplier(String name, Class<?>[] parameterTypes, Object[] params) {
		return new Supplier<T>() {
			@Override
			public T get() {
				return getInstance(name, parameterTypes, params);
			}
		};
	}

	boolean isInstance(Class<?> clazz, Class<?>[] parameterTypes);

	<T> T getInstance(Class<T> clazz, Class<?>[] parameterTypes, Object[] params);

	default <T> Supplier<T> getInstanceSupplier(Class<T> clazz, Class<?>[] parameterTypes, Object[] params) {
		return new Supplier<T>() {
			@Override
			public T get() {
				return getInstance(clazz, parameterTypes, params);
			}
		};
	}
}
