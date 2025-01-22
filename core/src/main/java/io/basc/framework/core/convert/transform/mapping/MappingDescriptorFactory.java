package io.basc.framework.core.convert.transform.mapping;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.NonNull;

public interface MappingDescriptorFactory<SD extends FieldDescriptor, SM extends MappingDescriptor<? extends SD>> {
	SM getMappingDescriptor(@NonNull TypeDescriptor requiredType);
}
