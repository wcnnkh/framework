package io.basc.framework.orm;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ReversibleMapperFactory;

public interface ObjectMapper<S, E extends Throwable> extends ObjectRelationalMapper, ReversibleMapperFactory<S, E> {

	default <R> R convert(S source, EntityStructure<? extends Property> targetStructure) throws E {
		return convert(source, TypeDescriptor.forObject(source),
				TypeDescriptor.valueOf(targetStructure.getEntityClass()), targetStructure);
	}

	@SuppressWarnings("unchecked")
	default <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure) throws E {
		R target = (R) newInstance(targetType);
		transform(source, sourceType, target, targetType, targetStructure);
		return target;
	}

	default void transform(S source, Object target, EntityStructure<? extends Property> targetStructure) throws E {
		transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target), targetStructure);
	}

	void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure) throws E;
	
	@SuppressWarnings("unchecked")
	default <R extends S> R invert(Object source, TypeDescriptor sourceType,
			EntityStructure<? extends Property> sourceStructure, TypeDescriptor targetType) throws E {
		R target = (R) newInstance(targetType);
		reverseTransform(source, sourceType, sourceStructure, target, targetType);
		return target;
	}

	default void reverseTransform(Object source, EntityStructure<? extends Property> sourceStructure, S target)
			throws E, ConverterNotFoundException {
		reverseTransform(source, TypeDescriptor.forObject(source), sourceStructure, target,
				TypeDescriptor.forObject(target));
	}

	void reverseTransform(Object source, TypeDescriptor sourceType, EntityStructure<? extends Property> sourceStructure,
			S target, TypeDescriptor targetType) throws E, ConverterNotFoundException;
}