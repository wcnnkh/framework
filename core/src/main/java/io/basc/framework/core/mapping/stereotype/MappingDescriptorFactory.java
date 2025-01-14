package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.NonNull;

public interface MappingDescriptorFactory<D extends FieldDescriptor, T extends MappingDescriptor<? extends D>> {
	T getMappingDescriptor(@NonNull TypeDescriptor requiredType);
}
