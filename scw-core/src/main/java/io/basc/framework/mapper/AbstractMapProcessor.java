package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.Value;

public abstract class AbstractMapProcessor<S, T, E extends Throwable> implements
		Processor<S, T, E> {
	private ConversionService conversionService = Sys.env.getConversionService();
	private final TypeDescriptor typeDescriptor;
	private FieldFactory fieldFactory = MapperUtils.getFieldFactory();

	public AbstractMapProcessor(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T process(S source) throws E {
		if (isEntity(typeDescriptor.getType())) {
			// 如果是一个实体类
			return (T) mapEntity(source, typeDescriptor.getType(), null);
		}
		return (T) conversionService.convert(source,
				TypeDescriptor.forObject(source), typeDescriptor);
	}

	protected boolean isEntity(Class<?> type) {
		return !Value.isBaseType(type);
	}

	protected <V> V mapEntity(S source, Class<V> targetType,
			Field parentField) throws E {
		V target = Sys.env.getInstance(targetType);
		for (Field field : fieldFactory
				.getFields(targetType, parentField).entity().all()) {
			Object value;
			if (isEntity(field.getSetter().getType())) {
				value = mapEntity(source, field.getSetter().getType(), field);
			} else {
				value = mapField(source, field);
			}
			field.getSetter().set(target, value, conversionService);
		}
		return target;
	}

	protected abstract Object mapField(S source, Field field) throws E;
}
