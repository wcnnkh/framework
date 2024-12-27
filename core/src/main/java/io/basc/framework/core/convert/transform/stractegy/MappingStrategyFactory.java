package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Accesstor;
import io.basc.framework.core.convert.transform.Mapping;
import lombok.NonNull;

@FunctionalInterface
public interface MappingStrategyFactory<K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable> {
	MappingStrategy<K, V, M, E> getMappingStrategy(@NonNull TypeDescriptor targetType);
}
