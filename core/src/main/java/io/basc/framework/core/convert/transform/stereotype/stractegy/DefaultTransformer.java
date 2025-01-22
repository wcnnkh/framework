package io.basc.framework.core.convert.transform.stereotype.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.core.convert.transform.config.Transformers;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.TemplateTransformer;
import io.basc.framework.core.convert.transform.stereotype.config.TemplateFactoryRegistry;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DefaultTransformer<X, Y, K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends DefaultTemplateTransformerFactory<K, SV, S, TV, T, E> implements Transformer<X, Y, E> {
	private final TemplateFactoryRegistry<X, K, SV, S> sourceTemplateProvider = new TemplateFactoryRegistry<>();
	private final TemplateFactoryRegistry<Y, K, TV, T> targetTemplateProvider = new TemplateFactoryRegistry<>();
	private final Transformers<X, Y, E, Transformer<? super X, ? super Y, ? extends E>> transformers = new Transformers<>();

	protected S getSourceTemplate(@NonNull X source, @NonNull TypeDescriptor sourceType) {
		return sourceTemplateProvider.getTemplate(source, sourceType);
	}

	protected T getTargetTemplate(@NonNull Y target, @NonNull TypeDescriptor targetType) {
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
		if (transformers.canTransform(sourceType, targetType)) {
			return true;
		}
		return hasSourceTemplate(sourceType) && hasTargetTemplate(targetType);
	}

	@Override
	public void transform(@NonNull X source, @NonNull TypeDescriptor sourceType, @NonNull Y target,
			@NonNull TypeDescriptor targetType) throws E {
		if (transformers.canTransform(sourceType, targetType)) {
			transformers.transform(source, sourceType, target, targetType);
			return;
		}

		TemplateTransformer<K, SV, S, TV, T, E> templateTransformer = getTemplateTransformer(targetType);
		transform(source, sourceType, target, targetType, templateTransformer);
	}

	public void transform(@NonNull X source, @NonNull TypeDescriptor sourceType, @NonNull Y target,
			@NonNull TypeDescriptor targetType,
			TemplateTransformer<? super K, ? super SV, ? super S, ? super TV, ? super T, ? extends E> templateTransformer)
			throws E {
		S sourceTemplate = getSourceTemplate(source, sourceType);
		T targetTemplate = getTargetTemplate(target, targetType);
		templateTransformer.transform(sourceTemplate, sourceType, targetTemplate, targetType);
	}
}
