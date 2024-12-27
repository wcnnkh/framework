package io.basc.framework.core.convert.transform.stractegy;

import java.util.Iterator;

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
public class MappingFilterChain<K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable>
		implements MappingStrategy<K, V, M, E> {
	@NonNull
	private final Iterator<? extends MappingFilter<K, V, M, E>> iterator;
	private MappingStrategy<K, V, M, E> nextStrategy;

	@Override
	public void doMapping(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping,
			@NonNull KeyValue<K, V> entry, MappingContext<K, V, M> targetContext, @NonNull M targetMapping) throws E {
		if (iterator.hasNext()) {
			iterator.next().doFilter(sourceContext, sourceMapping, entry, targetContext, targetMapping, this);
		} else if (nextStrategy != null) {
			nextStrategy.doMapping(sourceContext, sourceMapping, entry, targetContext, targetMapping);
		}
	}

}
