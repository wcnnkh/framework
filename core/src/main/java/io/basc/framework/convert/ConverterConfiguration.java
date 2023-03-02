package io.basc.framework.convert;

import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionFactory;

public interface ConverterConfiguration {

	default Object newInstance(TypeDescriptor type) {
		if (type.isMap()) {
			return CollectionFactory.createMap(type.getType(), type.getMapKeyTypeDescriptor().getType(), 16);
		}

		if (type.isCollection()) {
			return CollectionFactory.createCollection(type.getType(), type.getElementTypeDescriptor().getType(), 16);
		}

		return ReflectionApi.newInstance(type.getType());
	}

	default boolean canDirectlyConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType != null && targetType != null && targetType.isAssignableTo(sourceType)) {
			return true;
		}

		if (targetType.getType() == Object.class) {
			return true;
		}
		return false;
	}
}
