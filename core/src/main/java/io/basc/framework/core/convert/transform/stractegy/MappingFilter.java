package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.transform.Accesstor;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.core.convert.transform.MappingContext;
import io.basc.framework.util.KeyValue;
import lombok.NonNull;

public interface MappingFilter<K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable> {
	void doFilter(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping, @NonNull KeyValue<K, V> entry,
			MappingContext<K, V, M> targetContext, @NonNull M targetMapping,
			MappingStrategy<K, V, M, E> mappingStrategy) throws E;
}
