package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public abstract class AbstractConditionalTransformer extends AbstractTransformer implements ConditionalTransformer {
	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ConditionalTransformer.super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
