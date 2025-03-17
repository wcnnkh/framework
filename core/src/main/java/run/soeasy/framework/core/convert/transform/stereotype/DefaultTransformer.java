package run.soeasy.framework.core.convert.transform.stereotype;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.transform.Transformer;
import run.soeasy.framework.core.convert.transform.config.Transformers;

@Getter
@Setter
public class DefaultTransformer<X, Y, K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends Transformers<X, Y, E, Transformer<? super X, ? super Y, ? extends E>> {
	private final TemplateFactoryRegistry<X, K, SV, S> sourceTemplateProvider = new TemplateFactoryRegistry<>();

	private final TemplateFactoryRegistry<Y, K, TV, T> targetTemplateProvider = new TemplateFactoryRegistry<>();

	@NonNull
	private TemplateTransformer<K, SV, S, TV, T, E> templateTransformer = new TemplateTransformer<>();

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		if (super.canTransform(sourceType, targetType)) {
			return true;
		}
		return hasSourceTemplate(sourceType) && hasTargetTemplate(targetType);
	}

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

	public void transform(@NonNull X source, @NonNull TypeDescriptor sourceType, @NonNull Y target,
			@NonNull TypeDescriptor targetType) throws E {
		if (super.canTransform(sourceType, targetType)) {
			super.transform(source, sourceType, target, targetType);
			return;
		}

		S sourceTemplate = getSourceTemplate(source, sourceType);
		T targetTemplate = getTargetTemplate(target, targetType);
		templateTransformer.transform(sourceTemplate, sourceType, targetTemplate, targetType);
	}
}
