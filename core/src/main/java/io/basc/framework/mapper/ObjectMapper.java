package io.basc.framework.mapper;

import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

public interface ObjectMapper<S, E extends Throwable> extends ReversibleMapperFactory<S, E>, StructureFactory {

	default boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor) {
		return isEntity(descriptor.getType());
	}

	@SuppressWarnings("unchecked")
	@Override
	default <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return (R) source;
		}

		if (isConverterRegistred(targetType.getType())) {
			return ReversibleMapperFactory.super.convert(source, sourceType, targetType);
		}
		return convert(source, sourceType, targetType, getStructure(targetType.getType()));
	}

	@Override
	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		if (isTransformerRegistred(targetType.getType())) {
			ReversibleMapperFactory.super.transform(source, sourceType, target, targetType);
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
		return convert(source, sourceType, targetType, targetStructure, getValueProcessor(source, sourceType));
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
		if (isEntity(targetType.getType())) {
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
		transform(source, sourceType, target, targetType, targetStructure, getValueProcessor(source, sourceType));
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
			if (isEntity(targetType.getType(), property.getSetter())) {
				TypeDescriptor valueTypeDescriptor = new TypeDescriptor(property.getSetter());
				Object entity = convert(source, sourceType, valueTypeDescriptor,
						getStructure(valueTypeDescriptor.getType()).setParentField(property), valueProcessor);
				if (entity != null) {
					value = new AnyValue(entity, valueTypeDescriptor);
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

	default Processor<Field, Value, E> getValueProcessor(S source) throws E {
		return getValueProcessor(source, TypeDescriptor.forObject(source));
	}

	Processor<Field, Value, E> getValueProcessor(S source, TypeDescriptor sourceType) throws E;

	@SuppressWarnings("unchecked")
	@Override
	default <R extends S> R invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return (R) source;
		}

		if (isInverterRegistred(sourceType.getType())) {
			return ReversibleMapperFactory.super.invert(source, sourceType, targetType);
		}

		return invert(source, sourceType, getStructure(sourceType.getType()), targetType);
	}

	@SuppressWarnings("unchecked")
	default <R extends S> R invert(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			TypeDescriptor targetType) throws E {
		R target = (R) newInstance(targetType);
		reverseTransform(source, sourceType, sourceStructure, target, targetType);
		return target;
	}

	@Override
	default void reverseTransform(Object source, TypeDescriptor sourceType, S target, TypeDescriptor targetType)
			throws E {
		if (isReverseTransformerRegistred(sourceType.getType())) {
			ReversibleMapperFactory.super.reverseTransform(source, sourceType, target, targetType);
			return;
		}
		reverseTransform(source, sourceType, getStructure(sourceType.getType()), target, targetType);
	}

	default void reverseTransform(Object source, Structure<? extends Field> sourceStructure, S target)
			throws E, ConverterNotFoundException {
		reverseTransform(source, TypeDescriptor.forObject(source), sourceStructure, target,
				TypeDescriptor.forObject(target));
	}

	default void reverseTransform(Object source, TypeDescriptor sourceType, Structure<? extends Field> sourceStructure,
			S target, TypeDescriptor targetType) throws E, ConverterNotFoundException {
		reverseTransform(source, sourceType, sourceStructure.stream().iterator(), target, targetType);
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
			reverseTransform(sourceType, property.getParameter(source), property, target, targetType);
		}
	}

	default void reverseTransform(Collection<? extends Parameter> sourceParameters, S target) throws E {
		reverseTransform(sourceParameters, target, TypeDescriptor.forObject(target));
	}

	default void reverseTransform(Collection<? extends Parameter> sourceParameters, S target, TypeDescriptor targetType)
			throws E {
		if (CollectionUtils.isEmpty(sourceParameters)) {
			return;
		}

		for (Parameter parameter : sourceParameters) {
			reverseTransform(parameter, target, targetType);
		}
	}

	default void reverseTransform(TypeDescriptor sourceType, Parameter sourceParameter, Field parameterField, S target,
			TypeDescriptor targetType) throws E {
		if (isEntity(sourceType.getType(), sourceParameter)) {
			reverseTransform(sourceParameter.get(), sourceParameter.getTypeDescriptor(),
					getStructure(sourceParameter.getType()).setParentField(parameterField), target, targetType);
		} else {
			reverseTransform(sourceParameter, target, targetType);
		}
	}

	default void reverseTransform(Parameter sourceParameter, S target) throws E {
		reverseTransform(sourceParameter, target, TypeDescriptor.forObject(target));
	}

	void reverseTransform(Parameter sourceParameter, S target, TypeDescriptor targetType) throws E;
}
