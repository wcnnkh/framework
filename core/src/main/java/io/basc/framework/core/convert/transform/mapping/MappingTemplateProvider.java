package io.basc.framework.core.convert.transform.mapping;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.stereotype.config.TemplateProvider;
import lombok.NonNull;

public interface MappingTemplateProvider<SD extends FieldDescriptor, SM extends MappingDescriptor<? extends SD>>
		extends MappingDescriptorFactory<SD, SM>,
		TemplateProvider<Object, Object, Field<SD>, MappingTemplate<SD, ? extends SM>> {

	@Override
	default MappingTemplate<SD, ? extends SM> getTemplate(@NonNull Object source,
			@NonNull TypeDescriptor requiredType) {
		SM sm = getMappingDescriptor(requiredType);
		if (sm == null) {
			return null;
		}

		return new MappingTemplate<>(sm, source);
	}

	@Override
	default boolean hasTemplate(@NonNull TypeDescriptor requiredType) {
		return getMappingDescriptor(requiredType) != null;
	}
}