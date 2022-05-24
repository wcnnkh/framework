package io.basc.framework.orm;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.MapperFactory;

public interface ObjectMapper<S, E extends Throwable> extends ObjectRelationalMapper, MapperFactory<S, E> {

	default <R> R mapping(S source, EntityStructure<? extends Property> targetStructure) throws E {
		return mapping(source, TypeDescriptor.forObject(source),
				TypeDescriptor.valueOf(targetStructure.getEntityClass()), targetStructure);
	}

	<R> R mapping(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure) throws E;

	default void transform(S source, Object target, EntityStructure<? extends Property> targetStructure) throws E {
		transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target), targetStructure);
	}

	void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure) throws E;
}