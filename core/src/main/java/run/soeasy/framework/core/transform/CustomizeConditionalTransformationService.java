package run.soeasy.framework.core.transform;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.TypeMapping;

@Getter
public class CustomizeConditionalTransformationService implements ConditionalTransformationService {
	private final TypeMapping typeMapping;
	private final ReversibleTransformer<Object, Object> reversibleTransformer;

	@SuppressWarnings("unchecked")
	public CustomizeConditionalTransformationService(@NonNull TypeMapping typeMapping,
			@NonNull ReversibleTransformer<?, ?> reversibleTransformer) {
		this.typeMapping = typeMapping;
		this.reversibleTransformer = (ReversibleTransformer<Object, Object>) reversibleTransformer;
	}

	public CustomizeConditionalTransformationService reversed() {
		return new CustomizeConditionalTransformationService(typeMapping.reversed(), reversibleTransformer.reversed());
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
			return reversibleTransformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		}

		if (canTransform(targetTypeDescriptor, sourceTypeDescriptor)) {
			return reversibleTransformer.reverseTransform(target, targetTypeDescriptor, source, sourceTypeDescriptor);
		}
		return false;
	}

	@Override
	public Set<TypeMapping> getTransformableTypeMappings() {
		TypeDescriptor sourceTypeDescriptor = TypeDescriptor.valueOf(typeMapping.getKey());
		TypeDescriptor targetTypeDescriptor = TypeDescriptor.valueOf(typeMapping.getValue());
		boolean b1 = reversibleTransformer.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
		boolean b2 = reversibleTransformer.canTransform(targetTypeDescriptor, sourceTypeDescriptor);
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
