package io.basc.framework.core.convert.service.support;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.annotation.ZoneOffset;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.ConversionServiceAware;
import lombok.Data;
import lombok.NonNull;

@Data
class LocalDateTimeConversion implements ConversionService, ConversionServiceAware {
	private ConversionService conversionService;

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null) {
			return false;
		}

		return LocalDateTime.class.isAssignableFrom(sourceType.getType())
				&& conversionService.canConvert(sourceType.convert(ResolvableType.forClass(Date.class)), targetType);
	}

	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor targetType) throws ConversionException {
		Object source = value.get();
		TypeDescriptor sourceType = value.getTypeDescriptor();
		ZoneOffset offset = targetType.getAnnotation(ZoneOffset.class);
		java.time.ZoneOffset zoneOffset = offset == null ? OffsetDateTime.now().getOffset()
				: java.time.ZoneOffset.of(offset.value());
		long milli = ((LocalDateTime) source).toInstant(zoneOffset).toEpochMilli();
		return conversionService.convert(new Date(milli), sourceType.convert(ResolvableType.forClass(Date.class)),
				targetType);
	}
}
