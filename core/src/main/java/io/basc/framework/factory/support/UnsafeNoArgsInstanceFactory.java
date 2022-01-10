package io.basc.framework.factory.support;

import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.ClassUtils;

public class UnsafeNoArgsInstanceFactory extends AbstractNoArgsInstanceFactory {
	public static final UnsafeNoArgsInstanceFactory INSTANCE = new UnsafeNoArgsInstanceFactory();

	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}

		if (!isInstance(type)) {
			return null;
		}

		return ReflectionApi.newInstance(type);
	}

	public boolean isInstance(Class<?> clazz) {
		return !clazz.isPrimitive() && ClassUtils.isAvailable(clazz) && ReflectionUtils.isAvailable(clazz)
				&& ReflectionApi.isInstance(clazz);
	}
}
