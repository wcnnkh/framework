package io.basc.framework.convert.transform.entity;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.transform.Accessor;
import io.basc.framework.convert.transform.Mapping;
import io.basc.framework.convert.transform.MappingContext;
import io.basc.framework.convert.transform.MappingFactory;
import io.basc.framework.convert.transform.MappingStrategy;
import io.basc.framework.convert.transform.MappingStrategyFactory;
import io.basc.framework.util.KeyValue;
import lombok.NonNull;

public interface EntityTransformer<M extends Mapping<K, V>, K, V extends Accessor, T, E extends Throwable>
		extends MappingFactory<K, V, T, E>, MappingStrategyFactory<M, K, V, E> {
	@Override
	M getMapping(T transform, TypeDescriptor typeDescriptor);

	@Override
	default void transform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		transform(source, sourceType, target, targetType, getMappingStrategy(targetType));
	}

	default void transform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			MappingStrategy<M, K, V, E> mappingStrategy) throws E {
		M sourceMapping = getMapping(source, sourceType);
		M targetMapping = getMapping(target, targetType);
		transform(null, sourceMapping, sourceType, null, targetMapping, targetType, mappingStrategy);
	}

	default void transform(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping,
			@NonNull TypeDescriptor sourceTypeDescriptor, MappingContext<K, V, M> targetContext,
			@NonNull M targetMapping, @NonNull TypeDescriptor targetTypeDescriptor,
			@NonNull MappingStrategy<M, K, V, E> mappingStrategy) throws E {
		for (KeyValue<K, V> sourceKeyValue : sourceMapping) {
			mappingStrategy.mapping(sourceContext, sourceMapping, sourceTypeDescriptor, sourceKeyValue, targetContext,
					targetMapping, targetTypeDescriptor);
		}
	}
}
