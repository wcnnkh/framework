package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.transform.Access;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.core.convert.transform.MappingContext;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class MappingFilters<K, V extends Access, M extends Mapping<K, V>, E extends Throwable>
		extends ConfigurableServices<MappingFilter<K, V, M, E>> implements MappingFilter<K, V, M, E> {

	@Override
	public void doFilter(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping, @NonNull KeyValue<K, V> entry,
			MappingContext<K, V, M> targetContext, @NonNull M targetMapping,
			MappingStrategy<K, V, M, E> mappingStrategy) throws E {
		MappingFilterChain<K, V, M, E> chain = new MappingFilterChain<>(this.iterator(), mappingStrategy);
		chain.doMapping(sourceContext, sourceMapping, entry, targetContext, targetMapping);
	}

}
