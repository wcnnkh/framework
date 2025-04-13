package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class TemplateWriteFilters<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
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
