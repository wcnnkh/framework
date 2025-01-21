package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.config.TemplateFactoryRegistry;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ObjectTransformer<K, V extends Accessor, T extends Template<K, ? extends V>, E extends Throwable>
		extends DefaultTransformer<Object, Object, K, V, T, V, T, E> {
	private final TemplateFactoryRegistry<Object, K, V, T> objectTemplateProvider = new TemplateFactoryRegistry<>();

	@Override
	protected T getSourceTemplate(@NonNull Object source, @NonNull TypeDescriptor sourceType) {
		T template = objectTemplateProvider.getTemplate(source, sourceType);
		if (template == null) {
			template = super.getSourceTemplate(source, sourceType);
		}
		return template;
	}

	@Override
	protected T getTargetTemplate(@NonNull Object target, @NonNull TypeDescriptor targetType) {
		T template = objectTemplateProvider.getTemplate(target, targetType);
		if (template == null) {
			template = super.getTargetTemplate(target, targetType);
		}
		return template;
	}

	@Override
	protected boolean hasSourceTemplate(@NonNull TypeDescriptor requiredType) {
		return objectTemplateProvider.hasTemplate(requiredType) || super.hasSourceTemplate(requiredType);
	}

	@Override
	protected boolean hasTargetTemplate(@NonNull TypeDescriptor requiredType) {
		return objectTemplateProvider.hasTemplate(requiredType) || super.hasTargetTemplate(requiredType);
	}
}
