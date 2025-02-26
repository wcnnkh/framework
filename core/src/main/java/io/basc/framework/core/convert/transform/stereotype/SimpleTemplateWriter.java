package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.support.IdentityConversionService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class SimpleTemplateWriter<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		implements TemplateWriter<K, SV, S, TV, T, E> {
	@NonNull
	private ConversionService conversionService = new IdentityConversionService();
	
	@Override
	public boolean writeTo(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull SV sourceElement, @NonNull TV targetAccessor)
			throws E {
		if (!conversionService.canConvert(sourceElement.getTypeDescriptor(),
				targetAccessor.getRequiredTypeDescriptor())) {
			return false;
		}

		Object value = conversionService.convert(sourceElement, targetAccessor.getRequiredTypeDescriptor());
		if (value == null && targetAccessor.isRequired()) {
			return false;
		}

		targetAccessor.set(value);
		return true;
	}
}
