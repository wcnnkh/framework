package io.basc.framework.transform.factory;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.PropertiesTransformer;

@FunctionalInterface
public interface PropertiesTransformerFactory<E extends Throwable> extends PropertiesTransformer<Object, E> {

	<T> PropertiesTransformer<T, E> getPropertiesTransformer(Class<? extends T> requiredType);

	@Override
	default Properties getProperties(Object transform, TypeDescriptor typeDescriptor) {
		PropertiesTransformer<Object, E> transformer = getPropertiesTransformer(typeDescriptor.getType());
		if (transform == null) {
			return null;
		}
		return transformer.getProperties(transformer, typeDescriptor);
	}

	@Override
	default boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getPropertiesTransformer(sourceType.getType()) != null
				&& getPropertiesTransformer(targetType.getType()) != null;
	}

	@Override
	default boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getPropertiesTransformer(sourceType.getType()) != null
				&& getPropertiesTransformer(targetType.getType()) != null;
	}
}
