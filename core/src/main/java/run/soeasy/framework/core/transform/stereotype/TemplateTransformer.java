package run.soeasy.framework.core.transform.stereotype;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.Transformer;

@Getter
public class TemplateTransformer<K, V extends Accessor, R extends Template<K, V>, S, T, E extends Throwable>
		extends DefaultTemplateWriter<K, V, R> implements Transformer<S, T, E> {
	private final TemplateFactoryRegistry<S, K, V, R> sourceTemplateProvider = new TemplateFactoryRegistry<>();
	private final TemplateFactoryRegistry<T, K, V, R> targetTemplateProvider = new TemplateFactoryRegistry<>();

	protected R getSourceTemplate(@NonNull S source, @NonNull TypeDescriptor sourceType) {
		return sourceTemplateProvider.getTemplate(source, sourceType);
	}

	protected R getTargetTemplate(@NonNull T target, @NonNull TypeDescriptor targetType) {
		return targetTemplateProvider.getTemplate(target, targetType);
	}

	protected boolean hasSourceTemplate(@NonNull TypeDescriptor requiredType) {
		return sourceTemplateProvider.hasTemplate(requiredType);
	}

	protected boolean hasTargetTemplate(@NonNull TypeDescriptor requiredType) {
		return targetTemplateProvider.hasTemplate(requiredType);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return hasSourceTemplate(sourceType) && hasTargetTemplate(targetType);
	}

	@Override
	public boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E {
		R sourceTemplate = getSourceTemplate(source, sourceType);
		R targetTemplate = getTargetTemplate(target, targetType);
		return writeTo(new TemplateContext<>(sourceTemplate), new TemplateContext<>(targetTemplate));
	}
}
