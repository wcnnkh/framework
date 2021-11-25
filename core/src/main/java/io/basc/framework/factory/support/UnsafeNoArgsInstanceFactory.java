package io.basc.framework.factory.support;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.UnsafeUtils;

public class UnsafeNoArgsInstanceFactory extends AbstractNoArgsInstanceFactory {
	static {
		if (!UnsafeUtils.isSupport()) {
			throw new NotSupportedException("UnsafeNoArgsInstanceFactory");
		}
	}

	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}

		if (!isInstance(type)) {
			return null;
		}

		try {
			return type.cast(UnsafeUtils.allocateInstance(type));
		} catch (Exception e) {
			throw new InstanceException(type.getName(), e);
		}
	}

	public boolean isInstance(Class<?> clazz) {
		return !clazz.isPrimitive() && ClassUtils.isAvailable(clazz) && ReflectionUtils.isAvailable(clazz);
	}
}
