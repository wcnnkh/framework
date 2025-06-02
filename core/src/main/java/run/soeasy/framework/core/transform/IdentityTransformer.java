package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface IdentityTransformer<T> extends Transformer<T, T> {
	@Override
	default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return sourceTypeDescriptor.getType() == targetTypeDescriptor.getType();
	}
}
