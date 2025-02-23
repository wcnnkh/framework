package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.Source;
import lombok.NonNull;

public interface PropertyFactory {
	boolean hasProperty(@NonNull PropertyDescriptor propertyDescriptor);

	Source getProperty(@NonNull PropertyDescriptor propertyDescriptor);
}
