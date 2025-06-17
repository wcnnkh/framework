package run.soeasy.framework.core.transform;

import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;

public interface ConditionalTransformer extends Transformer {
	Set<TypeMapping> getTransformableTypeMappings();

	@Override
	default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return getTransformableTypeMappings().stream()
				.anyMatch((e) -> e.canConvert(sourceTypeDescriptor, targetTypeDescriptor));
	}

}
