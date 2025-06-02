package run.soeasy.framework.core.transform;

import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.TypeMapping;

public interface ConditionalTransformer<S, T> extends Transformer<S, T> {
	Set<TypeMapping> getTransformableTypeMappings();

	@Override
	default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		Class<?> sourceType = sourceTypeDescriptor.getType();
		Class<?> targetType = targetTypeDescriptor.getType();
		return getTransformableTypeMappings().stream().anyMatch((e) -> e.test(sourceType, targetType));
	}

}
