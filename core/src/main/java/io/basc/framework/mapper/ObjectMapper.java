package io.basc.framework.mapper;

import java.util.Iterator;
import java.util.List;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.ReversibleMapperFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.value.Value;

public interface ObjectMapper<S, E extends Throwable>
		extends ReversibleMapperFactory<S, E>, MappingFactory, ObjectAccessFactoryRegistry<E> {

	default Object convert(S source, TypeDescriptor targetType, Mapping<? extends Field> targetMapping) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType, targetMapping);
	}

	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws E {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			ObjectAccess<E> sourceAccess = getObjectAccess(source, sourceType);
			return convert(sourceAccess, targetType, targetMapping);
		}
		return convert(source, sourceType, getMapping(sourceType.getType()), targetType, targetMapping);
	}

	default Object convert(ObjectAccess<E> sourceAccess, TypeDescriptor targetType) throws E {
		Object target = newInstance(targetType);
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			ObjectAccess<E> targetAccess = getObjectAccess(target, targetType);
			transform(sourceAccess, targetAccess);
		} else {
			transform(sourceAccess, target, targetType, getMapping(targetType.getType()));
		}
		return target;
	}

	default Object convert(ObjectAccess<E> sourceAccess, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws E {
		Object target = newInstance(targetType);
		if (target == null) {
			return null;
		}

		transform(sourceAccess, target, targetType, targetMapping);
		return target;
	}

	default Object convert(S source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			TypeDescriptor targetType, Mapping<? extends Field> targetMapping) throws E {
		Object target = newInstance(targetType);
		if (target == null) {
			return null;
		}

		transform(source, sourceType, sourceMapping, target, targetType, targetMapping);
		return target;
	}

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
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

		transform(source, sourceType, target, targetType);
		return target;
	}

	@SuppressWarnings("unchecked")
	default <R extends S> R invert(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceStructure,
			TypeDescriptor targetType) throws E {
		R target = (R) newInstance(targetType);
		if (target == null) {
			return null;
		}

		transform(source, sourceType, sourceStructure, target, targetType);
		return target;
	}

	@SuppressWarnings("unchecked")
	@Override
	default S invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
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

		reverseTransform(source, sourceType, target, targetType);
		return target;
	}

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		if (isReverseTransformerRegistred(sourceType.getType())) {
			ReversibleMapperFactory.super.reverseTransform(source, sourceType, target, targetType);
			return;
		}

		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, getObjectAccess(target, targetType));
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType);
			return;
		}

		if (targetType.getType() == sourceType.getType()
				|| sourceType.getType().isAssignableFrom(targetType.getType())) {
			copy(target, targetType, source, sourceType, getMapping(targetType.getType()));
			return;
		}

		transform(target, targetType, getMapping(targetType.getType()), source, sourceType,
				getMapping(sourceType.getType()));
	}

	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType, Mapping<? extends Field> targetMapping) {
		List<? extends Field> sourceFields = sourceMapping.getElements().toList();
		List<? extends Field> targetFields = targetMapping.getElements().toList();
		Value sourceInstance = Value.of(source, sourceType);
		Value targetInstance = Value.of(target, targetType);
		// source元素是否比right元素多
		boolean sourceGtRight = sourceFields.size() > targetFields.size();
		// 用少的元素做迭代
		for (Field f1 : sourceGtRight ? targetFields : sourceFields) {
			Iterator<? extends Field> iterator = sourceGtRight ? sourceFields.iterator() : targetFields.iterator();
			while (iterator.hasNext()) {
				Field f2 = iterator.next();
				Field sourceField;
				Field targetField;
				if (sourceGtRight) {
					targetField = f1;
					sourceField = f2;
				} else {
					targetField = f2;
					sourceField = f1;
				}

				if (!targetField.test(sourceField)) {
					continue;
				}

				Parameter sourceValue = sourceField.get(sourceInstance);
				if (sourceValue == null) {
					continue;
				}
				targetField.set(targetInstance, sourceValue);
			}
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws E {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType, targetMapping);
		} else {
			transform(source, sourceType, getMapping(sourceType.getType()), target, targetType, targetMapping);
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, ObjectAccess<? extends E> targetAccess) throws E {
		if (source == null) {
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), targetAccess);
			return;
		}

		Mapping<? extends Field> sourceMapping = getMapping(sourceType.getType());
		transform(source, sourceType, sourceMapping, targetAccess);
	}

	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			Object target, TypeDescriptor targetType) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, sourceMapping, getObjectAccess(target, targetType));
		} else {
			transform(source, sourceType, getMapping(targetType.getType()), target, targetType,
					getMapping(targetType.getType()));
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, Mapping<? extends Field> sourceMapping,
			ObjectAccess<? extends E> targetAccess) throws E {
		Value sourceInstance = Value.of(source, sourceType);
		for (Field field : sourceMapping.getElements()) {
			Parameter parameter = field.get(sourceInstance);
			if (parameter == null) {
				continue;
			}
			targetAccess.set(parameter);
		}
	}

	default void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(sourceAccess, getObjectAccess(target, targetType));
			return;
		}

		transform(sourceAccess, target, targetType, getMapping(targetType.getType()));
	}

	default void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Mapping<? extends Field> targetMapping) throws E {
		Value targetInstance = Value.of(target, targetType);
		for (Field field : targetMapping.getElements()) {
			Parameter parameter = sourceAccess.get(field.getName());
			if (parameter == null) {
				continue;
			}

			field.set(targetInstance, parameter);
		}
	}

	default void transform(ObjectAccess<E> sourceAccess, ObjectAccess<? extends E> targetAccess) throws E {
		sourceAccess.copy(targetAccess);
	}

	@Override
	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		if (isTransformerRegistred(targetType.getType())) {
			ReversibleMapperFactory.super.transform(source, sourceType, target, targetType);
			return;
		}

		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType);
			return;
		}

		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform((Object) source, sourceType, getObjectAccess(target, targetType));
			return;
		}

		if (targetType.getType() == sourceType.getType()
				|| targetType.getType().isAssignableFrom(sourceType.getType())) {
			copy(source, sourceType, target, targetType, getMapping(targetType.getType()));
			return;
		}

		transform(source, sourceType, getMapping(sourceType.getType()), target, targetType,
				getMapping(targetType.getType()));
	}

	default <T> void copy(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			Mapping<? extends Field> mapping) throws E {
		Value sourceInstance = Value.of(source, sourceType);
		Value targetInstance = Value.of(target, targetType);
		for (Field field : mapping.getElements()) {
			Parameter value = field.get(sourceInstance);
			if (value == null) {
				continue;
			}
			field.set(targetInstance, value);
		}
	}
}
