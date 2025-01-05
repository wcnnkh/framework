package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.Converters;
import io.basc.framework.core.mapping.Mapper;
import io.basc.framework.core.mapping.config.ConfigurableInstanceFactory;
import lombok.NonNull;

public class StereotypeMapper<F extends FieldDescriptor, M extends MappingDescriptor<F>> extends
		StereotypeTransformer<F, M, ConversionException> implements Mapper<Object, Object, ConversionException> {
	private final ConfigurableInstanceFactory instanceFactory = new ConfigurableInstanceFactory();
	private final Converters<Object, Object, ConversionException, Converter<? super Object, ? extends Object, ? extends ConversionException>> converters = new Converters<>();

	public ConfigurableInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public Converters<Object, Object, ConversionException, Converter<? super Object, ? extends Object, ? extends ConversionException>> getConverters() {
		return converters;
	}

	@Override
	public boolean canInstantiated(@NonNull TypeDescriptor requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public Object newInstance(@NonNull TypeDescriptor requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return converters.canConvert(sourceType, targetType) || Mapper.super.canConvert(sourceType, targetType);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException, ConversionFailedException {
		if (converters.canConvert(sourceType, targetType)) {
			return converters.convert(source, sourceType, targetType);
		}
		return Mapper.super.convert(source, sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	public <T> T convert(Object source, Class<? extends T> targetType) {
		return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType));
	}
}
