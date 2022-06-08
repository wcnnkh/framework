package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.env.Sys;
import io.basc.framework.value.Value;

public abstract class AbstractMapper<S, T, E extends Throwable> implements Mapper<S, T, E> {
	private ConversionService conversionService = Sys.env.getConversionService();
	private final TypeDescriptor typeDescriptor;

	public AbstractMapper(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends T> R convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		return (R) convert(source, sourceType, targetType, null);
	}

	@Override
	public final void transform(S source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		transform(source, sourceType, target, targetType, null);
	}

	public Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType, Field parentField) throws E {
		if (isEntity(targetType.getType())) {
			Object target = ReflectionApi.newInstance(targetType.getType());
			transform(source, sourceType, target, targetType, parentField);
			return target;
		}
		return getConversionService().convert(source, TypeDescriptor.forObject(source), typeDescriptor);

	}

	public void transform(S source, TypeDescriptor sourceType, Object target, TypeDescriptor targetType,
			Field parentField) throws E {
		for (Field field : Fields.getFields(targetType.getType(), parentField).withSuperclass().entity().all()) {
			Object value;
			if (isEntity(field.getSetter().getType())) {
				value = convert(source, sourceType, new TypeDescriptor(field.getSetter()), field);
			} else {
				value = mapField(source, sourceType, field);
			}
			field.getSetter().set(target, value, getConversionService());
		}
	}

	protected boolean isEntity(Class<?> type) {
		return !Value.isBaseType(type);
	}

	protected abstract Object mapField(S source, TypeDescriptor sourceType, Field field) throws E;
}
