package io.basc.framework.core.env.config;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.support.DefaultConversionService;
import lombok.Getter;
import lombok.Setter;

public class ConfigurableConversionService extends DefaultConversionService {
	@Getter
	@Setter
	private volatile ConversionService parentConversionService;

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (parentConversionService == null) {
			return super.convert(source, sourceType, targetType);
		}

		if (super.canConvert(sourceType, targetType)) {
			return super.convert(source, sourceType, targetType);
		}
		return parentConversionService.convert(source, sourceType, targetType);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return super.canConvert(sourceType, targetType)
				|| (parentConversionService != null && parentConversionService.canConvert(sourceType, targetType));
	}

}
