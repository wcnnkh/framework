package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.mapping.config.ObjectMapper;
import lombok.NonNull;

public class StereotypeMapper<D extends FieldDescriptor, T extends MappingDescriptor<? extends D>>
		extends ObjectMapper<Object, Accessor, Template<Object, ? extends Accessor>, ConversionException>
		implements MappingDescriptorFactory<D, T> {
	private final MappingDescriptorRegistry<D, T> mappingDescriptorRegistry = new MappingDescriptorRegistry<>();

	@Override
	public T getMappingDescriptor(@NonNull TypeDescriptor requiredType) {
		return mappingDescriptorRegistry.getMappingDescriptor(requiredType);
	}

	public MappingDescriptorRegistry<D, T> getMappingDescriptorRegistry() {
		return mappingDescriptorRegistry;
	}

	public MappingTemplate<D, T> getMappingTemplate(@NonNull Object object, @NonNull TypeDescriptor requiredType) {
		T template = getMappingDescriptor(requiredType);
		return template == null ? null : new MappingTemplate<>(template, object);
	}

	@Override
	public Template<Object, ? extends Accessor> getObjectTemplate(@NonNull Object object,
			@NonNull TypeDescriptor requiredType) {
		Template<Object, ? extends Accessor> template = super.getObjectTemplate(object, requiredType);
		return template == null ? getMappingTemplate(object, requiredType) : template;
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
