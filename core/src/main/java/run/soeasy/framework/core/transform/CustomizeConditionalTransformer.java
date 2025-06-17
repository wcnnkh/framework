package run.soeasy.framework.core.transform;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;

@Getter
@RequiredArgsConstructor
public class CustomizeConditionalTransformer implements ConditionalTransformer {
	@NonNull
	private final Set<TypeMapping> transformableTypeMappings;
	@NonNull
	private final Transformer transformer;

	public CustomizeConditionalTransformer(TypeMapping typeMapping, Transformer transformer) {
		this(Collections.singleton(typeMapping), transformer);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ConditionalTransformer.super.canTransform(sourceTypeDescriptor, targetTypeDescriptor)
				&& transformer.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}
}
