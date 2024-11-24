package io.basc.framework.transform.strategy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;

@FunctionalInterface
public interface PropertiesTransformStrategy {
	void doTransform(@Nullable PropertiesTransformContext sourceContext, Properties sourceProperties,
			TypeDescriptor sourceTypeDescriptor, Property sourceProperty,
			@Nullable PropertiesTransformContext targetContext, Properties targetProperties,
			TypeDescriptor targetTypeDescriptor);
}
