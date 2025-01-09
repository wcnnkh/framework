package io.basc.framework.core.execution.resolver;

import io.basc.framework.core.convert.Value;
import io.basc.framework.core.mapping.PropertyDescriptor;

public interface PropertyFactory {
	boolean hasProperty(PropertyDescriptor propertyDescriptor);

	Value getProperty(PropertyDescriptor propertyDescriptor);
}
