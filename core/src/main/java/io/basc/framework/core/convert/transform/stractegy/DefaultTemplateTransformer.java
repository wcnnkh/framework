package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.IdentityConversionService;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.TemplateTransformer;
import io.basc.framework.core.convert.transform.TransformContext;
import io.basc.framework.util.Elements;
import lombok.NonNull;

public class DefaultTemplateTransformer<K, SV extends Value, S extends Template<K, SV>, TV extends Accessor, T extends Template<K, TV>, E extends Throwable>
		implements TemplateTransformer<K, SV, S, TV, T, E> {
	@NonNull
	private ConversionService conversionService = new IdentityConversionService();

	@Override
	public void transform(TransformContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, K index, SV accessor, TransformContext<K, TV, T> targetContext,
			@NonNull T target, @NonNull TypeDescriptor targetType) throws E {
		Elements<TV> accessors = target.getAccessors(index);
		for (TV targetAccessor : accessors) {
			if (targetAccessor.isWriteable()) {
				continue;
			}

			if (!conversionService.canConvert(accessor.getTypeDescriptor(),
					targetAccessor.getRequiredTypeDescriptor())) {
				continue;
			}

			Object value = conversionService.convert(accessor, targetAccessor.getRequiredTypeDescriptor());
			if (value == null && targetAccessor.isRequired()) {
				continue;
			}

			targetAccessor.set(value);
		}
	}
}
