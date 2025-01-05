package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.stractegy.ObjectTransformer;
import lombok.NonNull;

public class StereotypeTransformer<D extends FieldDescriptor, T extends MappingDescriptor<D>, E extends Throwable>
		extends ObjectTransformer<Object, Field<D>, MappingTemplate<D, T>, E>
		implements MappingDescriptorFactory<D, T> {
	private final MappingDescriptorRegistry<D, T> mappingDescriptorRegistry = new MappingDescriptorRegistry<>();

	@Override
	public T getMappingDescriptor(@NonNull TypeDescriptor requiredType) {
		return mappingDescriptorRegistry.getMappingDescriptor(requiredType);
	}

	public MappingDescriptorRegistry<D, T> getMappingDescriptorRegistry() {
		return mappingDescriptorRegistry;
	}

	@Override
	public MappingTemplate<D, T> getObjectTemplate(@NonNull Object object, @NonNull TypeDescriptor requiredType) {
		MappingTemplate<D, T> fieldTemplate = super.getObjectTemplate(object, requiredType);
		if (fieldTemplate == null) {
			T template = getMappingDescriptor(requiredType);
			if (template != null) {
				fieldTemplate = new MappingTemplate<>(template, object);
			}
		}
		return fieldTemplate;
	}

	@Override
	protected boolean hasSourceTemplate(@NonNull Class<?> sourceType) {
		return super.hasSourceTemplate(sourceType) || mappingDescriptorRegistry.containsTemplate(sourceType);
	}

	@Override
	protected boolean hasTargetTemplate(@NonNull Class<?> targetType) {
		return super.hasTargetTemplate(targetType) || mappingDescriptorRegistry.containsTemplate(targetType);
	}
}
