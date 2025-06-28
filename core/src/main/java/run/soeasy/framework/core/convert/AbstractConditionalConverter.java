package run.soeasy.framework.core.convert;

import lombok.NonNull;

public abstract class AbstractConditionalConverter extends AbstractConverter implements ConditionalConverter {
	
	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ConditionalConverter.super.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
