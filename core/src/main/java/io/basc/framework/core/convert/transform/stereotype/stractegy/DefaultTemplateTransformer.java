package io.basc.framework.core.convert.transform.stereotype.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.support.IdentityConversionService;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.TemplateTransformer;
import io.basc.framework.core.convert.transform.stereotype.TransformContext;
import io.basc.framework.util.check.PredicateRegistry;
import io.basc.framework.util.collections.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class DefaultTemplateTransformer<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		implements TemplateTransformer<K, SV, S, TV, T, E> {
	@NonNull
	private ConversionService conversionService = new IdentityConversionService();
	private final PredicateRegistry<K> indexPredicateRegistry = new PredicateRegistry<>();

	protected void doWrite(S template, SV source, Accessor target) {
		if (!conversionService.canConvert(source.getTypeDescriptor(), target.getRequiredTypeDescriptor())) {
			return;
		}

		Object value = conversionService.convert(source, target.getRequiredTypeDescriptor());
		if (value == null && target.isRequired()) {
			return;
		}

		target.set(value);
	}

	protected Elements<? extends TV> getTargetAccessors(K index, SV accessor, TransformContext<K, TV, T> targetContext,
			@NonNull T target, @NonNull TypeDescriptor targetType) {
		return target.getAccessors(index);
	}

	@Override
	public void transform(TransformContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, K index, SV accessor, TransformContext<K, TV, T> targetContext,
			@NonNull T target, @NonNull TypeDescriptor targetType) throws E {
		if (!indexPredicateRegistry.test(index)) {
			return;
		}

		Elements<? extends TV> accessors = getTargetAccessors(index, accessor, targetContext, target, targetType);
		for (TV targetAccessor : accessors) {
			if (targetAccessor.isWriteable()) {
				continue;
			}

			doWrite(source, accessor, targetAccessor);
		}
	}
}
