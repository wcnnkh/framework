package io.basc.framework.mapper;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.XUtils;

public interface ObjectMapper<S, E extends Throwable>
		extends ReversibleMapperFactory<S, E>, StructureFactory, ObjectAccessFactoryRegistry<E> {

	default Object convert(S source, Structure<? extends Field> targetStructure) throws E {
		return convert(source, TypeDescriptor.forObject(source),
				TypeDescriptor.valueOf(targetStructure.getSourceClass()), targetStructure);
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

	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure) throws E {
		Object target = newInstance(targetType);
		if (target == null) {
			return null;
		}
		transform(source, sourceType, target, targetType, targetStructure);
		return target;
	}

	default <T> void copy(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			Iterator<? extends Field> properties) throws E {
		while (properties.hasNext()) {
			Field field = properties.next();
			if (field.isSupportGetter() && field.isSupportSetter()) {
				Parameter value = field.getParameter(source);
				if (value == null || !value.isPresent()) {
					continue;
				}

				field.set(target, value);
			}
		}
	}

	default <T> void copy(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			Structure<? extends Field> structure) throws E {
		Iterator<? extends Structure<? extends Field>> iterator = structure.pages().iterator();
		while (iterator.hasNext()) {
			Structure<? extends Field> useStructure = iterator.next();
			copy(source, sourceType, target, targetType, useStructure.iterator());
		}
	}

	@SuppressWarnings("unchecked")
	default <R extends S> R invert(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
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
			copy(target, targetType, source, sourceType, getStructure(targetType.getType()));
			return;
		}

		transform(target, targetType, getStructure(targetType.getType()), source, sourceType,
				getStructure(sourceType.getType()));
	}

	default void transform(Object source, Structure<? extends Field> sourceStructure, Object target) throws E {
		transform(source, TypeDescriptor.valueOf(sourceStructure.getSourceClass()), sourceStructure, target,
				TypeDescriptor.forObject(target));
	}

	default void transform(Object source, TypeDescriptor sourceType, Iterator<? extends Field> sourceProperties,
			Object target, TypeDescriptor targetType, Iterator<? extends Field> targetProperties) throws E {
		Comparator<FieldDescriptor> comparator = (left, right) -> {
			if (left.getDeclaringClass() == right.getDeclaringClass()) {
				if (left.getType() == right.getType()) {
					return 0;
				}

				return right.getType().isAssignableFrom(left.getType()) ? 1 : -1;
			}
			return right.getDeclaringClass().isAssignableFrom(left.getDeclaringClass()) ? 1 : -1;
		};

		List<Field> targetFields = XUtils.stream(targetProperties).filter((e) -> e.isSupportSetter())
				.sorted((left, right) -> comparator.compare(left.getGetter(), right.getGetter()))
				.collect(Collectors.toList());
		XUtils.stream(sourceProperties).filter((e) -> e.isSupportGetter())
				.sorted((left, right) -> comparator.compare(left.getSetter(), right.getSetter()))
				.forEachOrdered((sourceField) -> {
					Parameter value = sourceField.getParameter(source);
					if (value == null || !value.isPresent()) {
						return;
					}

					Iterator<Field> iterator = targetFields.iterator();
					while (iterator.hasNext()) {
						Field targetField = iterator.next();
						if (sourceField.test(targetField)) {
							targetField.set(target, value);
						}
					}
				});
	}

	default void transform(Object source, TypeDescriptor sourceType, Iterator<? extends Field> sourceProperties,
			ObjectAccess<? extends E> targetAccess) throws E {
		while (sourceProperties.hasNext()) {
			Field field = sourceProperties.next();
			if (!field.isSupportGetter()) {
				continue;
			}

			Parameter parameter = field.getParameter(source);
			if (parameter == null || !parameter.isPresent()) {
				continue;
			}

			targetAccess.set(parameter);
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure) throws E {
		if (isObjectAccessFactoryRegistred(sourceType.getType())) {
			transform(getObjectAccess(source, sourceType), target, targetType, targetStructure);
		} else {
			transform(source, sourceType, getStructure(sourceType.getType()), target, targetType, targetStructure);
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

		Structure<? extends Field> sourceStructure = getStructure(sourceType.getType());
		transform(source, sourceType, sourceStructure, targetAccess);
	}

	default void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			Object target, TypeDescriptor targetType) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(source, sourceType, sourceStructure, getObjectAccess(target, targetType));
		} else {
			transform(source, sourceType, sourceStructure, target, targetType, getStructure(targetType.getType()));
		}
	}

	default void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			Object target, TypeDescriptor targetType, Structure<? extends Field> targetStructure) throws E {
		transform(source, sourceType, sourceStructure.streamAll().iterator(), target, targetType,
				targetStructure.streamAll().iterator());
	}

	default void transform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			ObjectAccess<? extends E> targetAccess) throws E {
		Iterator<? extends Structure<? extends Field>> iterator = sourceStructure.pages().iterator();
		while (iterator.hasNext()) {
			Structure<? extends Field> structure = iterator.next();
			transform(source, sourceType, structure.iterator(), targetAccess);
		}
	}

	default void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType) throws E {
		if (isObjectAccessFactoryRegistred(targetType.getType())) {
			transform(sourceAccess, getObjectAccess(target, targetType));
			return;
		}

		Structure<? extends Field> targetStructure = getStructure(targetType.getType());
		transform(sourceAccess, target, targetType, targetStructure);
	}

	default void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Iterator<? extends Field> targetProperties) throws E {
		while (targetProperties.hasNext()) {
			Field field = targetProperties.next();
			if (!field.isSupportSetter()) {
				continue;
			}

			Parameter parameter = sourceAccess.get(field.getName());
			if (parameter == null || !parameter.isPresent()) {
				continue;
			}

			field.set(target, parameter);
		}
	}

	default void transform(ObjectAccess<E> sourceAccess, Object target, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure) throws E {
		Iterator<? extends Structure<? extends Field>> iterator = targetStructure.pages().iterator();
		while (iterator.hasNext()) {
			Structure<? extends Field> structure = iterator.next();
			transform(sourceAccess, target, targetType, structure.iterator());
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
			copy(source, sourceType, target, targetType, getStructure(targetType.getType()));
			return;
		}

		transform(source, sourceType, getStructure(sourceType.getType()), target, targetType,
				getStructure(targetType.getType()));
	}
}
