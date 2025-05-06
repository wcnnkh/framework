package run.soeasy.framework.core.convert.date;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConversionServiceAware;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValue;
import run.soeasy.framework.core.type.ResolvableType;

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
				&& conversionService.canConvert(sourceType.convert(ResolvableType.forType(Date.class)), targetType)
				&& zoneOffsetResolver.resolveZoneOffset(() -> targetType) != null;
	}

	@Override
	public Object apply(@NonNull TypedValue value, @NonNull TargetDescriptor targetDescriptor) {
		Object source = value.get();
		TypeDescriptor sourceType = value.getReturnTypeDescriptor();
		java.time.ZoneOffset zoneOffset = zoneOffsetResolver
				.resolveZoneOffset(() -> targetDescriptor.getRequiredTypeDescriptor());
		if (zoneOffset == null) {
			zoneOffset = OffsetDateTime.now().getOffset();
		}

		long milli = ((LocalDateTime) source).toInstant(zoneOffset).toEpochMilli();
		return conversionService.convert(new Date(milli), sourceType.convert(ResolvableType.forType(Date.class)),
				targetDescriptor.getRequiredTypeDescriptor());
	}
}
