package io.basc.framework.core.convert.support.date;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import lombok.Data;
import lombok.NonNull;

@Data
public class LocalDateTimeConversion implements ConversionService, ConversionServiceAware {
	private ConversionService conversionService;
	@NonNull
	private final ZoneOffsetResolver zoneOffsetResolver;

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null) {
			return false;
		}

		return LocalDateTime.class.isAssignableFrom(sourceType.getType())
				&& conversionService.canConvert(sourceType.convert(ResolvableType.forClass(Date.class)), targetType)
				&& zoneOffsetResolver.resolveZoneOffset(() -> targetType) != null;
	}

	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor targetType) throws ConversionException {
		Object source = value.get();
		TypeDescriptor sourceType = value.getTypeDescriptor();
		java.time.ZoneOffset zoneOffset = zoneOffsetResolver.resolveZoneOffset(() -> targetType);
		if (zoneOffset == null) {
			zoneOffset = OffsetDateTime.now().getOffset();
		}

		long milli = ((LocalDateTime) source).toInstant(zoneOffset).toEpochMilli();
		return conversionService.convert(new Date(milli), sourceType.convert(ResolvableType.forClass(Date.class)),
				targetType);
	}
}
