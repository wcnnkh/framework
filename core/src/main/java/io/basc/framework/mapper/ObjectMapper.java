package io.basc.framework.mapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

public interface ObjectMapper<S, E extends Throwable> extends ReversibleMapperFactory<S, E> {

	default Structure<? extends Field> getStructure(Class<?> entityClass) {
		return Fields.getFields(entityClass);
	}

	void registerStructure(Class<?> entityClass, Structure<? extends Field> structure);

	@Nullable
	default Boolean isEntity(Class<?> type) {
		return !Value.isBaseType(type) && type != Object.class && ReflectionApi.isInstance(type)
				&& !Map.class.isAssignableFrom(type) && !Collection.class.isAssignableFrom(type);
	}

	default boolean isEntity(Class<?> entityClass, Field field) {
		if (field.isSupportSetter()) {
			Boolean b = isEntity(field.getSetter().getType());
			if (b != null) {
				return b;
			}
		}

		if (field.isSupportGetter()) {
			Boolean b = isEntity(field.getGetter().getType());
			if (b != null) {
				return b;
			}
		}
		return false;
	}

	@Override
	default <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (isConverterRegistred(targetType.getType())) {
			return ReversibleMapperFactory.super.convert(source, sourceType, targetType);
		}
		return convert(source, sourceType, targetType, getStructure(targetType.getType()));
	}

	@Override
	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		if (isTransformerRegistred(targetType.getType())) {
			ReversibleMapperFactory.super.convert(source, sourceType, targetType);
			return;
		}

		transform(source, sourceType, target, targetType, getStructure(targetType.getType()));
	}

	default <R> R convert(S source, Structure<? extends Field> targetStructure) throws E {
		return convert(source, TypeDescriptor.forObject(source),
				TypeDescriptor.valueOf(targetStructure.getSourceClass()), targetStructure);
	}

	default <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure) throws E {
		return convert(source, sourceType, targetType, targetStructure,
				getValueProcessor(source, sourceType, targetType));
	}

	@SuppressWarnings("unchecked")
	default <R, X extends Throwable> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure, Processor<Field, ? extends Value, X> valueProcessor)
			throws E, X {
		R target = (R) newInstance(targetType);
		transform(source, sourceType, target, targetType, targetStructure, valueProcessor);
		return target;
	}

	default void transform(S source, Object target, Structure<? extends Field> targetStructure) throws E {
		transform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target), targetStructure);
	}

	default <R, X extends Throwable> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Processor<Field, ? extends Value, X> valueProcessor) throws E, X {
		if (Boolean.TRUE.equals(isEntity(targetType.getType()))) {
			return convert(source, sourceType, targetType, getStructure(targetType.getType()), valueProcessor);
		}
		return convert(source, sourceType, targetType);
	}

	default <X extends Throwable> void transform(S source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, Processor<Field, ? extends Value, X> valueProcessor) throws E, X {
		transform(source, sourceType, target, targetType, getStructure(targetType.getType()), valueProcessor);
	}

	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Structure<? extends Field> targetStructure) throws E {
		transform(source, sourceType, target, targetType, targetStructure,
				getValueProcessor(source, sourceType, targetType));
	}

	default <X extends Throwable> void transform(S source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, Collection<? extends Field> fields,
			Processor<Field, ? extends Value, X> valueProcessor) throws E, X {
		transform(source, sourceType, target, targetType, fields.iterator(), valueProcessor);
	}

	default <X extends Throwable> void transform(S source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, Iterator<? extends Field> properties,
			Processor<Field, ? extends Value, X> valueProcessor) throws E, X {
		while (properties.hasNext()) {
			Field property = properties.next();
			if (property == null || !property.isSupportSetter()) {
				continue;
			}

			Value value = null;
			if (isEntity(targetType.getType(), property)) {
				TypeDescriptor valueTypeDescriptor = new TypeDescriptor(property.getSetter());
				Object entity = convert(source, sourceType, valueTypeDescriptor,
						getStructure(valueTypeDescriptor.getType()).setParentField(property), valueProcessor);
				if (entity != null) {
					value = new AnyValue(entity, valueTypeDescriptor, null);
				}
			} else {
				value = valueProcessor.process(property);
			}
			transform(source, sourceType, target, targetType, property, value);
		}
	}

	void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType, Field targetField,
			@Nullable Value sourceValue);

	default <X extends Throwable> void transform(S source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, Structure<? extends Field> targetStructure,
			Processor<Field, ? extends Value, X> valueProcessor) throws E, X {
		transform(source, sourceType, target, targetType, targetStructure.stream().iterator(), valueProcessor);
	}

	Processor<Field, Value, E> getValueProcessor(S source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E;

	@SuppressWarnings("unchecked")
	default <R extends S> R invert(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			TypeDescriptor targetType) throws E {
		R target = (R) newInstance(targetType);
		reverseTransform(source, sourceType, sourceStructure, target, targetType);
		return target;
	}

	default void reverseTransform(Object source, Structure<? extends Field> sourceStructure, S target)
			throws E, ConverterNotFoundException {
		reverseTransform(source, TypeDescriptor.forObject(source), sourceStructure, target,
				TypeDescriptor.forObject(target));
	}

	default void reverseTransform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			S target, TypeDescriptor targetType) throws E, ConverterNotFoundException {
		reverseTransform(sourceStructure, sourceType, sourceStructure.stream().iterator(), target, targetType);
	}

	default void reverseTransform(Object source, Collection<? extends Field> properties, S target)
			throws E, ConverterNotFoundException {
		reverseTransform(source, TypeDescriptor.forObject(source), properties.iterator(), target,
				TypeDescriptor.forObject(target));
	}

	default void reverseTransform(Object source, TypeDescriptor sourceType, Iterator<? extends Field> properties,
			S target, TypeDescriptor targetType) throws E, ConverterNotFoundException {
		while (properties.hasNext()) {
			Field property = properties.next();
			if (property == null || !property.isSupportGetter()) {
				continue;
			}
			reverseTransform(sourceType, property.getGetter().get(source), property, target, targetType);
		}
	}

	default void reverseTransform(TypeDescriptor sourceType, Object value, Field property, S target,
			TypeDescriptor targetType) throws E {
		if (isEntity(sourceType.getType(), property)) {
			reverseTransform(value, new TypeDescriptor(property.getGetter()),
					getStructure(sourceType.getType()).setParentField(property), target, targetType);
		} else {
			reverseTransform(value, property.getGetter().rename(property.getName()), target, targetType);
		}
	}

	default void reverseTransform(Object value, ParameterDescriptor descriptor, S target) throws E {
		reverseTransform(value, descriptor, target, TypeDescriptor.forObject(target));
	}

	void reverseTransform(Object value, ParameterDescriptor descriptor, S target, TypeDescriptor targetType) throws E;
}
