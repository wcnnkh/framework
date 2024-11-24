package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Access;
import io.basc.framework.core.convert.transform.Mapping;
import lombok.NonNull;

@FunctionalInterface
public interface MappingStrategyFactory<K, V extends Access, M extends Mapping<K, V>, E extends Throwable> {
	MappingStrategy<K, V, M, E> getMappingStrategy(@NonNull TypeDescriptor targetType);
}
