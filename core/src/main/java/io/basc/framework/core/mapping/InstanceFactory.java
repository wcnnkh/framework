package io.basc.framework.core.mapping;

import java.util.EnumSet;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 一个实例工厂
 * 
 * @author wcnnkh
 *
 */
public interface InstanceFactory {
	@RequiredArgsConstructor
	public static class CollectionFactory implements InstanceFactory {
		private final int initialCapacity;

		@Override
		public boolean canInstantiated(TypeDescriptor type) {
			return type.isCollection() || type.isMap();
		}

		@Override
		public Object newInstance(TypeDescriptor type) {
			if (type.isCollection()) {
				Class<?> enumClass = null;
				if (EnumSet.class.isAssignableFrom(type.getType())) {
					enumClass = type.getGeneric(0).getType();
				}
				return CollectionUtils.createCollection(type.getType(), enumClass, initialCapacity);
			}

			if (type.isMap()) {
				return CollectionUtils.createMap(type.getType(), type.getMapKeyTypeDescriptor().getType(),
						initialCapacity);
			}
			throw new UnsupportedOperationException(type.toString());
		}

	}

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

	boolean canInstantiated(@NonNull TypeDescriptor requiredType);

	Object newInstance(@NonNull TypeDescriptor requiredType);
}
