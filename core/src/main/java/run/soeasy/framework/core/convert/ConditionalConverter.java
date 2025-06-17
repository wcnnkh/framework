package run.soeasy.framework.core.convert;

import java.util.Set;

import lombok.NonNull;

public interface ConditionalConverter extends Converter {
	Set<TypeMapping> getConvertibleTypeMappings();

	@Override
	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return getConvertibleTypeMappings().stream()
				.anyMatch((e) -> e.canConvert(sourceTypeDescriptor, targetTypeDescriptor));
	}
}
