package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface ConditionalTransformationService
		extends TransformationService, ConditionalTransformer<Object, Object> {
	@Override
	default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ConditionalTransformer.super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
