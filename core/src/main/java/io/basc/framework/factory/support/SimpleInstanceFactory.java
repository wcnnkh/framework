package io.basc.framework.factory.support;

import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.DefaultClassLoaderAccessor;

public class SimpleInstanceFactory extends DefaultClassLoaderAccessor implements InstanceFactory {
	private final boolean unsafe;

	public SimpleInstanceFactory() {
		this(false);
	}

	public SimpleInstanceFactory(boolean unsafe) {
		this.unsafe = unsafe;
	}

	public boolean isUnsafe() {
		return unsafe;
	}

	@Override
	public <T> T getInstance(Class<? extends T> clazz) {
		if (isUnsafe()) {
			return ReflectionApi.newInstance(clazz);
		}
		return ReflectionUtils.newInstance(clazz);
	}

	@Override
	public boolean isInstance(Class<?> clazz) {
		if (isUnsafe()) {
			return ReflectionApi.isInstance(clazz);
		}

		if (clazz.isPrimitive() || !ClassUtils.isAvailable(clazz) || !ReflectionUtils.isAvailable(clazz)) {
			return false;
		}

		return ReflectionUtils.isInstance(clazz);
	}

	@Override
	public Object getInstance(String name) throws FactoryException {
		return getInstance(ClassUtils.resolveClassName(name, getClassLoader()));
	}

	@Override
	public boolean isInstance(String name) {
		return isInstance(ClassUtils.resolveClassName(name, getClassLoader()));
	}
}
