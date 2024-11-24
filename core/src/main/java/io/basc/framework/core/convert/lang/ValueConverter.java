package io.basc.framework.core.convert.lang;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.support.DefaultConversionService;
import lombok.Data;
import lombok.NonNull;

/**
 * TODO 没定义注册器
 * 
 * @author shuchaowen
 *
 */
@Data
public class ValueConverter implements ConversionService {
	private static volatile ValueConverter instance;

	public static ValueConverter getInstance() {
		if (instance == null) {
			synchronized (ValueConverter.class) {
				if (instance == null) {
					instance = new ValueConverter();
				}
			}
		}
		return instance;
	}

	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getConversionService().canConvert(sourceType, targetType);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return getConversionService().convert(source, sourceType, targetType);
	}
}
