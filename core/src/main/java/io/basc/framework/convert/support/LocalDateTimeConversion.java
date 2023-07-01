package io.basc.framework.convert.support;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.annotation.ZoneOffset;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.core.ResolvableType;

class LocalDateTimeConversion implements ConversionService, ConversionServiceAware {
	private ConversionService conversionService;

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null) {
			return false;
		}

		return LocalDateTime.class.isAssignableFrom(sourceType.getType())
				&& conversionService.canConvert(sourceType.convert(ResolvableType.forClass(Date.class)), targetType);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		ZoneOffset offset = targetType.getAnnotation(ZoneOffset.class);
		java.time.ZoneOffset zoneOffset = offset == null ? OffsetDateTime.now().getOffset()
				: java.time.ZoneOffset.of(offset.value());
		long milli = ((LocalDateTime) source).toInstant(zoneOffset).toEpochMilli();
		return conversionService.convert(new Date(milli), sourceType.convert(ResolvableType.forClass(Date.class)),
				targetType);
	}

}
