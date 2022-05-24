package io.basc.framework.orm.support;

import java.util.Iterator;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.Mapper;
import io.basc.framework.mapper.MapperFactory;
import io.basc.framework.mapper.SimpleMapperFactory;
import io.basc.framework.mapper.Transformer;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.ObjectMapper;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.repository.DefaultRepositoryMapper;
import io.basc.framework.util.stream.Processor;

public abstract class AbstractObjectMapper<S, E extends Throwable> extends DefaultRepositoryMapper
		implements ObjectMapper<S, E>, ConversionServiceAware {
	private final MapperFactory<S, E> mapperFactory = new SimpleMapperFactory<>();
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean isConverterRegistred(Class<?> type) {
		return mapperFactory.isConverterRegistred(type);
	}

	@Override
	public <T> Converter<S, T, E> getConverter(Class<? extends T> type) {
		return mapperFactory.getConverter(type);
	}

	@Override
	public <T> void registerConverter(Class<T> type, Converter<S, ? extends T, ? extends E> converter) {
		mapperFactory.registerConverter(type, converter);
	}

	@Override
	public boolean isTransformerRegistred(Class<?> type) {
		return mapperFactory.isTransformerRegistred(type);
	}

	@Override
	public <T> Transformer<S, T, E> getTransformer(Class<? extends T> type) {
		return mapperFactory.getTransformer(type);
	}

	@Override
	public <T> void registerTransformer(Class<T> type, Transformer<S, ? extends T, ? extends E> transformer) {
		mapperFactory.registerTransformer(type, transformer);
	}

	@Override
	public boolean isMapperRegistred(Class<?> type) {
		return mapperFactory.isMapperRegistred(type);
	}

	@Override
	public <T> Mapper<S, T, E> getMapper(Class<? extends T> type) {
		return mapperFactory.getMapper(type);
	}

	@Override
	public <T> void registerMapper(Class<T> type, Mapper<S, ? extends T, ? extends E> mapper) {
		mapperFactory.registerMapper(type, mapper);
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
				Object entity = mapping(source, sourceType, new TypeDescriptor(property.getField().getSetter()),
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
			return mapping(source, sourceType, targetType, getStructure(targetType.getType()));
		}
		return ObjectMapper.super.convert(source, sourceType, targetType);
	}

	@Override
	public <R> R mapping(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure) throws E {
		return mapping(source, sourceType, targetType, targetStructure, getValueProcessor(source, sourceType));
	}

	@SuppressWarnings("unchecked")
	public <R> R mapping(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			Processor<String, Object, E> valueProcessor) throws E, ConverterNotFoundException {
		Object target = newInstance(targetType);
		transform(source, sourceType, target, targetType, valueProcessor);
		return (R) target;
	}

	@SuppressWarnings("unchecked")
	public <R> R mapping(S source, TypeDescriptor sourceType, TypeDescriptor targetType,
			EntityStructure<? extends Property> targetStructure, Processor<String, Object, E> valueProcessor)
			throws E, ConverterNotFoundException {
		Object target = newInstance(targetType);
		transform(source, sourceType, target, targetType, targetStructure, valueProcessor);
		return (R) target;
	}

	protected Object getValue(S source, Processor<String, Object, E> valueProcessor, Property property) throws E {
		return property.getValueByNames(valueProcessor);
	}

	protected abstract Processor<String, Object, E> getValueProcessor(S source, TypeDescriptor sourceType);
}
