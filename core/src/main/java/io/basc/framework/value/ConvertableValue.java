package io.basc.framework.value;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;

final class ConvertableValue extends ObjectValue {
	private static final long serialVersionUID = 1L;
	private final Converter<? super Object, ? extends Object, ? extends ConversionException> converter;

	public ConvertableValue(Object value, TypeDescriptor typeDescriptor,
			Converter<? super Object, ? extends Object, ? extends ConversionException> converter) {
		super(value, typeDescriptor);
		this.converter = converter;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (converter instanceof ConversionService) {
			ConversionService conversionService = (ConversionService) converter;
			if (conversionService.canConvert(sourceType, targetType)) {
				return conversionService.convert(source, sourceType, targetType);
			} else {
				return super.convert(source, sourceType, targetType);
			}
		}

		return converter.convert(source, sourceType, targetType);
	}
}
