package io.basc.framework.mapper;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.ReversibleMapperFactory;
import io.basc.framework.convert.TypeDescriptor;

public interface ObjectMapper<S, E extends Throwable> extends ReversibleMapperFactory<S, E>, MappingFactory,
		ObjectAccessFactoryRegistry<E>, MappingStrategyFactory<E> {

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		return convert(source, sourceType, targetType, getMappingStrategy(targetType));
	}

	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			MappingStrategy<E> mappingStrategy) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}

		if (isConverterRegistred(targetType.getType())) {
			return ReversibleMapperFactory.super.convert(source, sourceType, targetType);
		}

		Object target = newInstance(targetType);
		if (target == null) {
			return null;
		}

		transform(source, sourceType, target, targetType, mappingStrategy);
		return target;
	}

	@Override
	default S invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		return invert(source, sourceType, targetType, getMappingStrategy(targetType));
	}

	@SuppressWarnings("unchecked")
	default S invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType,
			MappingStrategy<E> mappingStrategy) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return (S) source;
		}

		if (isInverterRegistred(sourceType.getType())) {
			return ReversibleMapperFactory.super.invert(source, sourceType, targetType);
		}

		S target = (S) newInstance(targetType);
		if (target == null) {
			return null;
		}

		reverseTransform(source, sourceType, target, targetType, mappingStrategy);
		return target;
	}

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		reverseTransform(source, sourceType, target, targetType, getMappingStrategy(targetType));
	}

	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType,
			MappingStrategy<E> mappingStrategy) throws E {
		if (isReverseTransformerRegistred(sourceType.getType())) {
			ReversibleMapperFactory.super.reverseTransform(source, sourceType, target, targetType);
			return;
		}

		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, getObjectAccess(target, targetType), mappingStrategy);
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType, mappingStrategy);
			return;
		}

		mappingStrategy.transform(target, targetType, getMapping(targetType.getType()), source, sourceType,
				getMapping(sourceType.getType()));
	}

	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType, MappingStrategy<E> mappingStrategy) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			mappingStrategy.transform(source, sourceType, sourceMapping, getObjectAccess(target, targetType));
		} else {
			mappingStrategy.transform(source, sourceType, getMapping(targetType.getType()), target, targetType,
					getMapping(targetType.getType()));
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping, MappingStrategy<E> mappingStrategy) throws E {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			mappingStrategy.transform(getObjectAccess(source, sourceType), target, targetType, targetMapping);
		} else {
			mappingStrategy.transform(source, sourceType, getMapping(sourceType.getType()), target, targetType,
					targetMapping);
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, ObjectAccess<? extends E> targetAccess,
			MappingStrategy<E> mappingStrategy) throws E {
		if (source == null) {
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			mappingStrategy.transform(getObjectAccess(source, sourceType), targetAccess);
			return;
		}

		mappingStrategy.transform(source, sourceType, getMapping(sourceType.getType()), targetAccess);
	}

	default void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			MappingStrategy<E> mappingStrategy) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			mappingStrategy.transform(sourceAccess, getObjectAccess(target, targetType));
			return;
		}
		mappingStrategy.transform(sourceAccess, target, targetType, getMapping(targetType.getType()));
	}

	@Override
	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		transform(source, sourceType, target, targetType, getMappingStrategy(targetType));
	}

	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			MappingStrategy<E> mappingStrategy) throws E, ConverterNotFoundException {
		if (isTransformerRegistred(targetType.getType())) {
			ReversibleMapperFactory.super.transform(source, sourceType, target, targetType);
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType, mappingStrategy);
			return;
		}

		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform((Object) source, sourceType, getObjectAccess(target, targetType), mappingStrategy);
			return;
		}

		mappingStrategy.transform(source, sourceType, getMapping(sourceType.getType()), target, targetType,
				getMapping(targetType.getType()));
	}
}
