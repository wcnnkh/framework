package run.soeasy.framework.core.transform.property;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.templates.MappingProvider;

public interface ObjectTemplateFactory<E extends Property>
		extends MappingProvider<Object, Object, PropertyAccessor, TypedProperties> {
	PropertyTemplate<E> getTemplate(Class<?> objectClass);

	@Override
	default boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return getTemplate(requiredType.getType()) != null;
	}

	@Override
	default TypedProperties getMapping(@NonNull Object source, @NonNull TypeDescriptor requiredType) {
		PropertyTemplate<E> propertyTemplate = getTemplate(requiredType.getType());
		if (propertyTemplate == null) {
			return null;
		}

		return new ObjectMapping<>(propertyTemplate, source);
	}
}
