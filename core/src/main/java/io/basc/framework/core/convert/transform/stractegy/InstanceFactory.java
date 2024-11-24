package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.Assert;

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

	default boolean canInstantiated(ResolvableType type) {
		return type != null && canInstantiated(TypeDescriptor.valueOf(type));
	}

	default Object newInstance(ResolvableType type) {
		Assert.requiredArgument(type != null, "type");
		return newInstance(TypeDescriptor.valueOf(type));
	}

	boolean canInstantiated(TypeDescriptor type);

	Object newInstance(TypeDescriptor type);
}
