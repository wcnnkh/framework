package io.basc.framework.convert.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.KeyValue;
import lombok.NonNull;

@FunctionalInterface
public interface MappingStrategy<M extends Mapping<K, ? extends V>, K, V extends Accessor, E extends Throwable> {
	void mapping(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping,
			@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull KeyValue<K, V> sourceKeyValue,
			MappingContext<K, V, M> targetContext, @NonNull M targetMapping,
			@NonNull TypeDescriptor targetTypeDescriptor) throws E;
}
