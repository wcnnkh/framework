package io.basc.framework.util;

import io.basc.framework.core.ResolvableType;

/**
 * 一个实例工厂
 * 
 * @author wcnnkh
 *
 */
public interface InstanceFactory {
	default boolean canInstantiated(Class<?> type) {
		if (type == null) {
			return false;
		}
		return canInstantiated(ResolvableType.forClass(type));
	}

	@SuppressWarnings("unchecked")
	default <T> T newInstance(Class<T> type) {
		Assert.requiredArgument(type != null, "type");
		return (T) newInstance(ResolvableType.forClass(type));
	}

	boolean canInstantiated(ResolvableType type);

	Object newInstance(ResolvableType type);
}
