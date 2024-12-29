package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.config.TemplateFactoryRegistry;
import lombok.NonNull;

public class ObjectTransformer<K, V extends Accessor, T extends Template<K, V>, E extends Throwable>
		extends DefaultTransformer<Object, Object, K, V, T, V, T, E> {
	private final TemplateFactoryRegistry<? super Object, ? extends K, ? extends V, ? extends T> templateFactoryRegistry = new TemplateFactoryRegistry<>();

	public T getObjectTemplate(@NonNull Object object, @NonNull TypeDescriptor requiredType) {
		return templateFactoryRegistry.getTemplate(object, requiredType);
	}

	@Override
	public final T getSourceTemplate(@NonNull Object source, @NonNull TypeDescriptor sourceType) {
		T template = super.getSourceTemplate(source, sourceType);
		if (template == null) {
			template = getObjectTemplate(source, sourceType);
		}
		return template;
	}

	@Override
	public final T getTargetTemplate(@NonNull Object target, @NonNull TypeDescriptor targetType) {
		T template = super.getTargetTemplate(target, targetType);
		if (template == null) {
			template = getObjectTemplate(target, targetType);
		}
		return template;
	}

	public TemplateFactoryRegistry<? super Object, ? extends K, ? extends V, ? extends T> getTemplateFactoryRegistry() {
		return templateFactoryRegistry;
	}

	@Override
	protected boolean hasSourceTemplate(@NonNull Class<?> sourceType) {
		return super.hasSourceTemplate(sourceType) || templateFactoryRegistry.containsTemplate(sourceType);
	}

	@Override
	protected boolean hasTargetTemplate(@NonNull Class<?> targetType) {
		return super.hasTargetTemplate(targetType) || templateFactoryRegistry.containsTemplate(targetType);
	}
}
