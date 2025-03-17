package run.soeasy.framework.core.convert.transform.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface MappingDescriptorFactory<SD extends FieldDescriptor, SM extends MappingDescriptor<? extends SD>> {
	SM getMappingDescriptor(@NonNull TypeDescriptor requiredType);
}
