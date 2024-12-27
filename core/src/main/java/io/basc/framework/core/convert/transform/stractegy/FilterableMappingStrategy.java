package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.transform.Accesstor;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.core.convert.transform.MappingContext;
import io.basc.framework.util.KeyValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class FilterableMappingStrategy<K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable>
		implements MappingStrategy<K, V, M, E> {
	@NonNull
	private final Iterable<? extends MappingFilter<K, V, M, E>> mappingFilters;
	private MappingStrategy<K, V, M, E> dottomlessStrategy;

	@Override
	public void doMapping(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping,
			@NonNull KeyValue<K, V> entry, MappingContext<K, V, M> targetContext, @NonNull M targetMapping) throws E {
		MappingFilterChain<K, V, M, E> chain = new MappingFilterChain<>(this.mappingFilters.iterator(),
				this.dottomlessStrategy);
		chain.doMapping(sourceContext, sourceMapping, entry, targetContext, targetMapping);
	}

}
