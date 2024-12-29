package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.stractegy.ObjectTransformer;
import lombok.NonNull;

public class StereotypeTransformer<D extends FieldDescriptor, T extends FieldDescriptorTemplate<D>, E extends Throwable>
		extends ObjectTransformer<Object, Field<D>, FieldTemplate<D, T>, E>
		implements FieldDescriptorTemplateFactory<D, T> {
	private final DefaultFieldDescriptorTemplateFactory<D, T> fieldDescriptorTemplateFactory = new DefaultFieldDescriptorTemplateFactory<>();

	@Override
	public T getFieldDescriptorTemplate(@NonNull TypeDescriptor requiredType) {
		return fieldDescriptorTemplateFactory.getFieldDescriptorTemplate(requiredType);
	}

	public DefaultFieldDescriptorTemplateFactory<D, T> getFieldDescriptorTemplateFactory() {
		return fieldDescriptorTemplateFactory;
	}

	@Override
	public FieldTemplate<D, T> getObjectTemplate(@NonNull Object object, @NonNull TypeDescriptor requiredType) {
		FieldTemplate<D, T> fieldTemplate = super.getObjectTemplate(object, requiredType);
		if (fieldTemplate == null) {
			T template = getFieldDescriptorTemplate(requiredType);
			if (template != null) {
				fieldTemplate = new FieldTemplate<>(template, object);
			}
		}
		return fieldTemplate;
	}

	@Override
	protected boolean hasSourceTemplate(@NonNull Class<?> sourceType) {
		return super.hasSourceTemplate(sourceType) || fieldDescriptorTemplateFactory.containsTemplate(sourceType);
	}

	@Override
	protected boolean hasTargetTemplate(@NonNull Class<?> targetType) {
		return super.hasTargetTemplate(targetType) || fieldDescriptorTemplateFactory.containsTemplate(targetType);
	}
}
