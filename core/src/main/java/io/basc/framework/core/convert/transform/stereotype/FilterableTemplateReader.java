package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.util.collections.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FilterableTemplateReader<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		implements TemplateReader<K, SV, S, TV, T, E> {
	@NonNull
	private final Iterable<? extends TemplateReadFilter<K, SV, S, TV, T, E>> templateReadFilters;
	private TemplateReader<K, SV, S, TV, T, E> templateReader;

	@Override
	public Elements<? extends SV> readFrom(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull TV targetAccessor) throws E {
		TemplateReaderChain<K, SV, S, TV, T, E> chain = new TemplateReaderChain<>(templateReadFilters.iterator(),
				templateReader);
		return chain.readFrom(sourceContext, source, sourceType, targetContext, target, targetType, index,
				targetAccessor);
	}

}
