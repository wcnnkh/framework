package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

public class ObjectTemplateTransformer<K, V extends ValueAccessor, T extends Template<K, V>, E extends Throwable>
		extends TemplateTransformer<K, V, T, Object, Object, E> {
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
