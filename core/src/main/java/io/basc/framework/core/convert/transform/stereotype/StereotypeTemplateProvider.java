package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.config.TemplateProvider;
import lombok.NonNull;

public interface StereotypeTemplateProvider<SD extends StereotypeDescriptor, SM extends StereotypeMapping<? extends SD>>
		extends StereotypeMappingFactory<SD, SM>,
		TemplateProvider<Object, Object, StereotypeProperty<SD>, StereotypeTemplate<SD, ? extends SM>> {

	@Override
	default StereotypeTemplate<SD, ? extends SM> getTemplate(@NonNull Object source,
			@NonNull TypeDescriptor requiredType) {
		SM sm = getStereotypeMapping(requiredType);
		if (sm == null) {
			return null;
		}

		return new StereotypeTemplate<>(sm, source);
	}

	@Override
	default boolean hasTemplate(@NonNull TypeDescriptor requiredType) {
		return getStereotypeMapping(requiredType) != null;
	}
}
