package io.basc.framework.factory.support;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.ClassUtils;

public class SimpleNoArgsInstanceFactory extends AbstractNoArgsInstanceFactory {

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return ReflectionUtils.newInstance(clazz);
	}

	@Override
	public boolean isInstance(Class<?> clazz) {
		if (clazz.isPrimitive() || !ClassUtils.isAvailable(clazz) || !ReflectionUtils.isAvailable(clazz)) {
			return false;
		}

		return ReflectionUtils.isInstance(clazz);
	}

}
