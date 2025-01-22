package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.Value;
import lombok.NonNull;

public interface PropertyFactory {
	boolean hasProperty(@NonNull PropertyDescriptor propertyDescriptor);

	Value getProperty(@NonNull PropertyDescriptor propertyDescriptor);
}
