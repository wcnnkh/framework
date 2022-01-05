package io.basc.framework.factory.support;

import io.basc.framework.core.reflect.Api;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.ClassUtils;

public class UnsafeNoArgsInstanceFactory extends AbstractNoArgsInstanceFactory {

	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}

		if (!isInstance(type)) {
			return null;
		}

		return Api.newInstance(type);
	}

	public boolean isInstance(Class<?> clazz) {
		return !clazz.isPrimitive() && ClassUtils.isAvailable(clazz) && ReflectionUtils.isAvailable(clazz)
				&& Api.isInstance(clazz);
	}
}
