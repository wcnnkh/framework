package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValueAccessor;

public class ObjectTemplateTransformer<K, V extends TypedValueAccessor, T extends Template<K, V>>
		extends TemplateTransformer<K, V, T, Object, Object> {
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
