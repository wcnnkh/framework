package io.basc.framework.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.transform.strategy.DefaultPropertiesTransformStrategy;
import io.basc.framework.transform.strategy.PropertiesTransformContext;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.transform.strategy.PropertiesTransformStrategyFactory;

@FunctionalInterface
public interface PropertiesTransformer<T, E extends Throwable>
		extends ReversibleTransformer<T, T, E>, PropertiesTransformStrategyFactory {
	Properties getProperties(T transform, TypeDescriptor typeDescriptor);

	@Override
	default PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor requiredTypeDescriptor) {
		return new DefaultPropertiesTransformStrategy();
	}

	@Override
	default void reverseTransform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		transform(source, sourceType, target, targetType, getPropertiesTransformStrategy(targetType));
	}

	default void transform(Properties sourceProperties, Properties targetProperties,
			PropertiesTransformStrategy propertiesTransformStrategy) {
		transform(null, sourceProperties, TypeDescriptor.valueOf(Properties.class), null, targetProperties,
				TypeDescriptor.valueOf(Properties.class), propertiesTransformStrategy);
	}

	default void transform(@Nullable PropertiesTransformContext sourceContext, Properties sourceProperties,
			TypeDescriptor sourceTypeDescriptor, @Nullable PropertiesTransformContext targetContext,
			Properties targetProperties, TypeDescriptor targetTypeDescriptor,
			PropertiesTransformStrategy propertiesTransformStrategy) {
		sourceProperties.getElements()
				.forEach((sourceProperty) -> propertiesTransformStrategy.doTransform(sourceContext, sourceProperties,
						sourceTypeDescriptor, sourceProperty, targetContext, targetProperties, targetTypeDescriptor));
	}

	@Override
	default void transform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		transform(source, sourceType, target, targetType, getPropertiesTransformStrategy(targetType));
	}

	default void transform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			PropertiesTransformStrategy propertiesTransformStrategy) {
		Properties sourceProperties = getProperties(source, sourceType);
		Properties targetProperties = getProperties(target, targetType);
		transform(null, sourceProperties, sourceType, null, targetProperties, targetType, propertiesTransformStrategy);
	}
}
