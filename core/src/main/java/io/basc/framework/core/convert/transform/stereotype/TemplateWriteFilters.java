package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class TemplateWriteFilters<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends ConfigurableServices<TemplateWriteFilter<K, SV, S, TV, T, E>>
		implements TemplateWriteFilter<K, SV, S, TV, T, E> {

	@Override
	public boolean writeTo(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull SV sourceElement, @NonNull TV targetAccessor,
			@NonNull TemplateWriter<K, SV, S, TV, T, E> writer) throws E {
		TemplateWriterChain<K, SV, S, TV, T, E> chain = new TemplateWriterChain<>(this.iterator(), writer);
		return chain.writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index, sourceElement,
				targetAccessor);
	}
}
