package run.soeasy.framework.core.convert;

import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.type.TypeMapping;

public interface ConditionalConverter<S, T> extends Converter<S, T> {
	Set<TypeMapping> getConvertibleTypeMappings();

	@Override
	default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		Class<?> sourceType = sourceTypeDescriptor.getType();
		Class<?> targetType = targetTypeDescriptor.getType();
		return getConvertibleTypeMappings().stream().anyMatch((e) -> e.test(sourceType, targetType));

	}
}
