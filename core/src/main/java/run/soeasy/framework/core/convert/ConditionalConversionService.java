package run.soeasy.framework.core.convert;

import lombok.NonNull;

public interface ConditionalConversionService extends ConversionService, ConditionalConverter<Object, Object> {
	@Override
	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ConditionalConverter.super.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
