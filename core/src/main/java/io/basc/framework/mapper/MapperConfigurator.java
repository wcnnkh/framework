package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.env.Sys;

public class MapperConfigurator<S, E extends Throwable> implements FieldFactory{
	private ConversionService conversionService;
	private FieldFactory fieldFactory;
	private final Mapper<S, E> mapper = new SimpleMapper<S, E>();

	public ConversionService getConversionService() {
		if (conversionService == null) {
			return Sys.env.getConversionService();
		}

		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public FieldFactory getFieldFactory() {
		if (fieldFactory == null) {
			return MapperUtils.getFieldFactory();
		}
		return fieldFactory;
	}

	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	public Mapper<S, E> getMapper() {
		return mapper;
	}

	@Override
	public Fields getFields(Class<?> entityClass, Field parentField) {
		return getFieldFactory().getFields(entityClass, parentField);
	}
}