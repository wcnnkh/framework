package io.basc.framework.mapper.property;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.transform.ReversibleTransformer;
import io.basc.framework.value.Property;

@FunctionalInterface
public interface PropertiesTransformer<T, E extends Throwable> extends ReversibleTransformer<T, T, E> {
	Properties getProperties(T transform, TypeDescriptor typeDescriptor);

	@Override
	default void transform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		Properties sourceProperties = getProperties(source, sourceType);
		Properties targetProperties = getProperties(target, targetType);
		copy(sourceProperties, targetProperties);
	}

	@Override
	default void reverseTransform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		Properties sourceProperties = getProperties(source, sourceType);
		Properties targetProperties = getProperties(target, targetType);
		copy(sourceProperties, targetProperties);
	}

	/**
	 * copy属性
	 * 
	 * @param sourceProperties
	 * @param targetProperties
	 */
	default void copy(Properties sourceProperties, Properties targetProperties) {
		for (Property property : sourceProperties.getElements()) {
			targetProperties.setElement(property);
		}
	}
}
