package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.Wrapper;

public interface TargetDescriptorWrapper<W extends TargetDescriptor> extends TargetDescriptor, Wrapper<W> {
	@Override
	default TypeDescriptor getRequiredTypeDescriptor() {
		return getSource().getRequiredTypeDescriptor();
	}

	@Override
	default boolean isRequired() {
		return getSource().isRequired();
	}
}