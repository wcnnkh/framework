package io.basc.framework.orm;

import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.ReversibleMapperFactory;
import io.basc.framework.util.stream.Processor;

public interface ObjectMapper<S, E extends Throwable> extends ObjectRelationalMapper, ReversibleMapperFactory<S, E> {

	default ConversionService getConversionService() {
		return Sys.env.getConversionService();
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

	@SuppressWarnings("unchecked")
	default <R, X extends Throwable> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Processor<Property, ? extends Object, X> valueProcessor) throws E, X {
		Object target = newInstance(targetType);
		transform(source, sourceType, target, targetType, valueProcessor);
		return (R) target;
	}

	default <X extends Throwable> void transform(S source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, Processor<Property, ? extends Object, X> valueProcessor) throws E, X {
		transform(source, sourceType, target, targetType, getStructure(targetType.getType()), valueProcessor);
	}

	default void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure) throws E {
		transform(source, sourceType, target, targetType, targetStructure, getValueProcessor(source, sourceType));
	}

	default <X extends Throwable> void transform(S source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, Collection<? extends Property> properties,
			Processor<Property, ? extends Object, X> valueProcessor) throws E, X {
		transform(source, sourceType, target, targetType, properties.iterator(), valueProcessor);
	}

	default <X extends Throwable> void transform(S source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, Iterator<? extends Property> properties,
			Processor<Property, ? extends Object, X> valueProcessor) throws E, X {
		while (properties.hasNext()) {
			Property property = properties.next();
			if (property.getField() == null || !property.getField().isSupportSetter()) {
				continue;
			}

			if (property.isEntity()) {
				Object entity = convert(source, sourceType, new TypeDescriptor(property.getField().getSetter()),
						valueProcessor);
				property.getField().getSetter().set(target, entity, getConversionService());
			} else {
				Object value = valueProcessor.process(property);
				if (value != null) {
					property.getField().getSetter().set(target, value, getConversionService());
				}
			}
		}
	}

	default <X extends Throwable> void transform(S source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType, EntityStructure<? extends Property> targetStructure,
			Processor<Property, ? extends Object, X> valueProcessor) throws E, X {
		transform(source, sourceType, target, targetType, targetStructure.stream().iterator(), valueProcessor);
	}

	Processor<Property, Object, E> getValueProcessor(S source, TypeDescriptor sourceType) throws E;

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

	default void reverseTransform(Object source, TypeDescriptor sourceType,
			EntityStructure<? extends Property> sourceStructure, S target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		reverseTransform(sourceStructure, sourceType, sourceStructure.stream().iterator(), target, targetType);
	}

	default void reverseTransform(Object source, Collection<? extends Property> properties, S target)
			throws E, ConverterNotFoundException {
		reverseTransform(source, TypeDescriptor.forObject(source), properties.iterator(), target,
				TypeDescriptor.forObject(target));
	}

	default void reverseTransform(Object source, TypeDescriptor sourceType, Iterator<? extends Property> properties,
			S target, TypeDescriptor targetType) throws E, ConverterNotFoundException {
		while (properties.hasNext()) {
			Property property = properties.next();
			if (property.getField() == null || !property.getField().isSupportGetter()) {
				continue;
			}
			reverseTransform(property.getField().get(source), property, target, targetType);
		}
	}

	default void reverseTransform(Object value, Property property, S target, TypeDescriptor targetType) throws E {
		if (property.isEntity()) {
			reverseTransform(value, new TypeDescriptor(property.getField().getGetter()), target, targetType);
		} else {
			reverseTransform(value, property.getField().getGetter().rename(property.getName()), target, targetType);
		}
	}
	
	default void reverseTransform(Object value, ParameterDescriptor descriptor, S target) throws E{
		reverseTransform(value, descriptor, target, TypeDescriptor.forObject(target));
	}

	void reverseTransform(Object value, ParameterDescriptor descriptor, S target, TypeDescriptor targetType) throws E;
}