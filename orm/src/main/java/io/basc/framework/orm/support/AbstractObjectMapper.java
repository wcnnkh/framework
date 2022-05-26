package io.basc.framework.orm.support;

import java.util.Iterator;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ReversibleMapperFactory;
import io.basc.framework.mapper.ReversibleMapperFactoryWrapper;
import io.basc.framework.mapper.SimpleReverseMapperFactory;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.ObjectMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.repository.DefaultRepositoryMapper;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Processor;

public abstract class AbstractObjectMapper<S, E extends Throwable> extends DefaultRepositoryMapper
		implements ObjectMapper<S, E>, ConversionServiceAware, ReversibleMapperFactoryWrapper<S, E> {
	private ReversibleMapperFactory<S, E> sourceConverterFactory = new SimpleReverseMapperFactory<>();
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	@Override
	public void setConversionService(@Nullable ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public ReversibleMapperFactory<S, E> getSourceConverterFactory() {
		return sourceConverterFactory;
	}

	public void setSourceConverterFactory(ReversibleMapperFactory<S, E> sourceConverterFactory) {
		Assert.requiredArgument(sourceConverterFactory != null, "sourceConverterFactory");
		this.sourceConverterFactory = sourceConverterFactory;
	}

	@Override
	public final void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure) throws E {
		Processor<String, Object, E> valueProcessor = getValueProcessor(source, sourceType);
		transform(source, sourceType, target, targetType, targetStructure, valueProcessor);
	}

	public void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Processor<String, Object, E> valueProcessor) throws E {
		transform(source, sourceType, target, targetType, getStructure(targetType.getType()), valueProcessor);
	}

	public void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure, Processor<String, Object, E> valueProcessor) throws E {
		Iterator<? extends Property> iterator = targetStructure.stream()
				.filter((e) -> e.getField() != null && e.getField().isSupportSetter()).iterator();
		while (iterator.hasNext()) {
			Property property = iterator.next();
			if (property.isEntity()) {
				Object entity = convert(source, sourceType, new TypeDescriptor(property.getField().getSetter()),
						valueProcessor);
				property.getField().set(target, entity, getConversionService());
			} else {
				Object value = getValue(source, valueProcessor, property);
				if (value != null) {
					property.getField().set(target, value, getConversionService());
				}
			}
		}
	}

	@Override
	public void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		if (!isTransformerRegistred(targetType.getType())) {
			transform(source, sourceType, target, targetType, getStructure(targetType.getType()));
			return;
		}
		ObjectMapper.super.transform(source, sourceType, target, targetType);
	}

	@Override
	public <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		if (!isConverterRegistred(targetType.getType())) {
			return convert(source, sourceType, targetType, getStructure(targetType.getType()));
		}
		return ObjectMapper.super.convert(source, sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	public <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Processor<String, Object, E> valueProcessor) throws E, ConverterNotFoundException {
		Object target = newInstance(targetType);
		transform(source, sourceType, target, targetType, valueProcessor);
		return (R) target;
	}

	@SuppressWarnings("unchecked")
	public <R> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure, Processor<String, Object, E> valueProcessor)
			throws E, ConverterNotFoundException {
		Object target = newInstance(targetType);
		transform(source, sourceType, target, targetType, targetStructure, valueProcessor);
		return (R) target;
	}

	protected Object getValue(S source, Processor<String, Object, E> valueProcessor, Property property) throws E {
		return property.getValueByNames(valueProcessor);
	}

	protected abstract Processor<String, Object, E> getValueProcessor(S source, TypeDescriptor sourceType) throws E;

	protected abstract void writeValue(Object value, ParameterDescriptor descriptor, S target) throws E;

	protected void writeValue(Object value, Property property, S target, TypeDescriptor targetType) throws E {
		writeValue(value, property.getField().getGetter().rename(property.getName()), target);
	}

	@Override
	public void reverseTransform(Object source, TypeDescriptor sourceType,
			EntityStructure<? extends Property> sourceStructure, S target, TypeDescriptor targetType)
			throws E, ConverterNotFoundException {
		Iterator<? extends Property> iterator = sourceStructure.stream()
				.filter((e) -> e.getField() != null && e.getField().isSupportGetter()).iterator();
		while (iterator.hasNext()) {
			Property property = iterator.next();
			Object value = property.getField().get(source);
			if (value == null) {
				continue;
			}

			if (property.isEntity()) {
				reverseTransform(value, new TypeDescriptor(property.getField().getGetter()), target, targetType);
			} else {
				writeValue(value, property, target, targetType);
			}
		}
	}

}
