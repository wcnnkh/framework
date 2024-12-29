package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.NonNull;

public interface FieldDescriptorTemplateFactory<D extends FieldDescriptor, T extends FieldDescriptorTemplate<D>> {
	T getFieldDescriptorTemplate(@NonNull TypeDescriptor requiredType);
}
