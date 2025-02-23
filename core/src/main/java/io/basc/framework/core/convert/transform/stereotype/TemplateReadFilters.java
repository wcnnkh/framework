package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class TemplateReadFilters<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends ConfigurableServices<TemplateReadFilter<K, SV, S, TV, T, E>>
		implements TemplateReadFilter<K, SV, S, TV, T, E> {

	@Override
	public Elements<? extends SV> readFrom(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull TV targetAccessor,
			TemplateReader<K, SV, S, TV, T, E> templateReader) throws E {
		TemplateReaderChain<K, SV, S, TV, T, E> chain = new TemplateReaderChain<>(iterator(), templateReader);
		return chain.readFrom(sourceContext, source, sourceType, targetContext, target, targetType, index,
				targetAccessor);
	}

}
