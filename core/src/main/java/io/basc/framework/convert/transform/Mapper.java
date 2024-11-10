package io.basc.framework.convert.transform;

import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;

@FunctionalInterface
public interface Mapper<S, T, E extends Throwable> extends Converter<S, T, E>, Transformer<S, T, E>, InstanceFactory {
	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return canInstantiated(targetType) && canTransform(sourceType, targetType);
	}

	@Override
	default boolean canInstantiated(TypeDescriptor type) {
		if (type == null) {
			return false;
		}

		return type.isMap() || type.isCollection() || ReflectionApi.isInstance(type.getType());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E, ConversionFailedException {
		if (this.canConvert(sourceType, targetType)) {
			T target = (T) newInstance(targetType);
			transform(source, sourceType, target, targetType);
			return target;
		}
		throw new ConversionFailedException(sourceType, targetType, source, null);
	}

	@Override
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
}