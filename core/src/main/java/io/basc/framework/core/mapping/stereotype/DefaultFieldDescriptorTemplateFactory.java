package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.spi.ServiceMap;
import lombok.NonNull;

public class DefaultFieldDescriptorTemplateFactory<D extends FieldDescriptor, T extends FieldDescriptorTemplate<D>>
		extends ServiceMap<T> implements FieldDescriptorTemplateFactory<D, T> {

	@Override
	public T getFieldDescriptorTemplate(@NonNull TypeDescriptor requiredType) {
		return getFirst(requiredType.getType());
	}

	public boolean containsTemplate(Class<?> requiredType) {
		return getFirst(requiredType) != null;
	}
}
