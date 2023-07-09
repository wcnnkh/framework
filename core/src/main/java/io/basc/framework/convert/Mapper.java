package io.basc.framework.convert;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.InstanceFactory;

public interface Mapper<S, T, E extends Throwable>
		extends ReversibleConverter<S, T, E>, ReversibleTransformer<S, T, E>, InstanceFactory {

	@Override
	default boolean canInstantiated(ResolvableType type) {
		return type != null && canInstantiated(TypeDescriptor.valueOf(type));
	}

	@Override
	default Object newInstance(ResolvableType type) {
		Assert.requiredArgument(type != null, "type");
		return newInstance(TypeDescriptor.valueOf(type));
	}

	default boolean canInstantiated(TypeDescriptor type) {
		if (type == null) {
			return false;
		}
		return type.isMap() || type.isCollection() || ReflectionApi.isInstance(type.getType());
	}

	default Object newInstance(TypeDescriptor type) {
		Assert.requiredArgument(type != null, "type");
		if (type.isMap()) {
			return CollectionFactory.createMap(type.getType(), type.getMapKeyTypeDescriptor().getType(), 16);
		}

		if (type.isCollection()) {
			return CollectionFactory.createCollection(type.getType(), type.getElementTypeDescriptor().getType(), 16);
		}

		return ReflectionApi.newInstance(type.getType());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (!canInstantiated(targetType)) {
			throw new ConversionFailedException(sourceType, targetType, source, null);
		}

		T target = (T) newInstance(targetType);
		transform(source, sourceType, target, targetType);
		return target;
	}

	@SuppressWarnings("unchecked")
	@Override
	default S invert(T source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (!canInstantiated(targetType)) {
			throw new ConversionFailedException(sourceType, targetType, source, null);
		}

		S target = (S) newInstance(targetType);
		reverseTransform(source, sourceType, target, targetType);
		return target;
	}
}
