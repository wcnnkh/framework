package run.soeasy.framework.core.convert.support;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConditionalConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public abstract class AbstractConditionalConverter extends AbstractConverter implements ConditionalConverter {
	
	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ConditionalConverter.super.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
