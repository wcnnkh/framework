package io.basc.framework.core.mapping;

import io.basc.framework.core.convert.Value;

public interface PropertyFactory {
	boolean hasProperty(PropertyDescriptor propertyDescriptor);

	Value getProperty(PropertyDescriptor propertyDescriptor);
}
