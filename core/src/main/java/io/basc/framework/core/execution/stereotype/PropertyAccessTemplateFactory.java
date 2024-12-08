package io.basc.framework.core.execution.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;

public interface PropertyAccessTemplateFactory<T extends PropertyAccessDescriptor> {
	PropertyAccessTemplate<T> getPropertyAccessTemplate(TypeDescriptor requiredTypeDescriptor);
}
