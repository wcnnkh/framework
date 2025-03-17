package run.soeasy.framework.core.convert.transform.stereotype;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.support.IdentityConversionService;

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
