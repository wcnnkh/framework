package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.TemplateTransformer;
import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.core.convert.transform.config.DefaultTemplateTransformerFactory;
import io.basc.framework.core.convert.transform.config.TemplateFactoryRegistry;
import io.basc.framework.core.convert.transform.config.Transformers;
import lombok.NonNull;

public class DefaultTransformer<A, B, K, SV extends Value, S extends Template<K, SV>, TV extends Accessor, T extends Template<K, TV>, E extends Throwable>
		extends DefaultTemplateTransformerFactory<K, SV, S, TV, T, E> implements Transformer<A, B, E> {
	private final TemplateFactoryRegistry<? super A, ? extends K, ? extends SV, ? extends S> sourceTemplateFactoryRegistry = new TemplateFactoryRegistry<>();
	private final TemplateFactoryRegistry<? super B, ? extends K, ? extends TV, ? extends T> targetTemplateFactoryRegistry = new TemplateFactoryRegistry<>();
	private final Transformers<A, B, E, Transformer<? super A, ? super B, ? extends E>> transformers = new Transformers<>();

	public Transformers<A, B, E, Transformer<? super A, ? super B, ? extends E>> getTransformers() {
		return transformers;
	}

	public S getSourceTemplate(@NonNull A source, @NonNull TypeDescriptor sourceType) {
		return sourceTemplateFactoryRegistry.getTemplate(source, sourceType);
	}

	public T getTargetTemplate(@NonNull B target, @NonNull TypeDescriptor targetType) {
		return targetTemplateFactoryRegistry.getTemplate(target, targetType);
	}

	protected boolean hasSourceTemplate(@NonNull Class<?> sourceType) {
		return sourceTemplateFactoryRegistry.containsTemplate(sourceType);
	}

	protected boolean hasTargetTemplate(@NonNull Class<?> targetType) {
		return targetTemplateFactoryRegistry.containsTemplate(targetType);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		if (transformers.canTransform(sourceType, targetType)) {
			return true;
		}
		return hasSourceTemplate(sourceType.getType()) && hasTargetTemplate(targetType.getType());
	}

	public TemplateFactoryRegistry<? super A, ? extends K, ? extends SV, ? extends S> getSourceTemplateFactoryRegistry() {
		return sourceTemplateFactoryRegistry;
	}

	public TemplateFactoryRegistry<? super B, ? extends K, ? extends TV, ? extends T> getTargetTemplateFactoryRegistry() {
		return targetTemplateFactoryRegistry;
	}

	@Override
	public void transform(@NonNull A source, @NonNull TypeDescriptor sourceType, @NonNull B target,
			@NonNull TypeDescriptor targetType) throws E {
		if (transformers.canTransform(sourceType, targetType)) {
			transformers.transform(source, sourceType, target, targetType);
			return;
		}

		TemplateTransformer<K, SV, S, TV, T, E> templateTransformer = getTemplateTransformer(targetType);
		transform(source, sourceType, target, targetType, templateTransformer);
	}

	public void transform(@NonNull A source, @NonNull TypeDescriptor sourceType, @NonNull B target,
			@NonNull TypeDescriptor targetType,
			TemplateTransformer<? super K, ? super SV, ? super S, ? super TV, ? super T, ? extends E> templateTransformer)
			throws E {
		S sourceTemplate = getSourceTemplate(source, sourceType);
		T targetTemplate = getTargetTemplate(target, targetType);
		templateTransformer.transform(sourceTemplate, sourceType, targetTemplate, targetType);
	}
}
