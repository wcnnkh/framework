package run.soeasy.framework.core.transform.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.stereotype.TemplateProvider;

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