package run.soeasy.framework.core.convert;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomizeConditionalConverter implements ConditionalConverter {
	@NonNull
	private final Set<TypeMapping> convertibleTypeMappings;
	@NonNull
	private final Converter converter;

	public CustomizeConditionalConverter(TypeMapping typeMapping, Converter converter) {
		this(Collections.singleton(typeMapping), converter);
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ConditionalConverter.super.canConvert(sourceTypeDescriptor, targetTypeDescriptor)
				&& converter.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}
}
