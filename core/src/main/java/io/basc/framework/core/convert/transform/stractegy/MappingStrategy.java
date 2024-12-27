package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.transform.Accesstor;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.core.convert.transform.MappingContext;
import io.basc.framework.util.KeyValue;
import lombok.NonNull;

@FunctionalInterface
public interface MappingStrategy<K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable> {

	default void doMapping(@NonNull M sourceMapping, @NonNull M targetMapping) throws E {
		doMapping(null, sourceMapping, null, targetMapping);
	}

	default void doMapping(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping,
			MappingContext<K, V, M> targetContext, @NonNull M targetMapping) throws E {
		for (KeyValue<K, V> entry : sourceMapping.getMembers()) {
			doMapping(sourceContext, sourceMapping, entry, targetContext, targetMapping);
		}
	}

	void doMapping(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping, @NonNull KeyValue<K, V> entry,
			MappingContext<K, V, M> targetContext, @NonNull M targetMapping) throws E;
}
