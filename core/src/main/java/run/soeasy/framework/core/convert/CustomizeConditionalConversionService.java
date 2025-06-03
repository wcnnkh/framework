package run.soeasy.framework.core.convert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.type.TypeMapping;

@Getter
public class CustomizeConditionalConversionService implements ConditionalConversionService {
	private final TypeMapping typeMapping;
	private final ReversibleConverter<Object, Object> reversibleConverter;

	@SuppressWarnings("unchecked")
	public CustomizeConditionalConversionService(@NonNull TypeMapping typeMapping,
			@NonNull ReversibleConverter<?, ?> reversibleConverter) {
		this.typeMapping = typeMapping;
		this.reversibleConverter = (ReversibleConverter<Object, Object>) reversibleConverter;
	}

	public CustomizeConditionalConversionService reversed() {
		return new CustomizeConditionalConversionService(typeMapping.reversed(), reversibleConverter.reversed());
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return reversibleConverter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
		}

		if (canConvert(targetTypeDescriptor, sourceTypeDescriptor)) {
			return reversibleConverter.reverseConvert(source, targetTypeDescriptor, sourceTypeDescriptor);
		}
		throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public Set<TypeMapping> getConvertibleTypeMappings() {
		TypeDescriptor sourceTypeDescriptor = TypeDescriptor.valueOf(typeMapping.getKey());
		TypeDescriptor targetTypeDescriptor = TypeDescriptor.valueOf(typeMapping.getValue());
		boolean b1 = reversibleConverter.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
		boolean b2 = reversibleConverter.canConvert(targetTypeDescriptor, sourceTypeDescriptor);
		if (b1) {
			if (b2) {
				Set<TypeMapping> sets = new HashSet<>(1, 1);
				sets.add(typeMapping);
				sets.add(typeMapping.reversed());
				return sets;
			} else {
				return Collections.singleton(typeMapping);
			}
		} else {
			if (b2) {
				return Collections.singleton(typeMapping.reversed());
			} else {
				return Collections.emptySet();
			}
		}
	}
}
