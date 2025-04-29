package run.soeasy.framework.core.convert;

import run.soeasy.framework.core.Wrapper;

public interface SourceDescriptor {
	@FunctionalInterface
	public static interface SourceDescriptorWrapper<W extends SourceDescriptor>
			extends SourceDescriptor, Wrapper<W> {
		@Override
		default TypeDescriptor getReturnTypeDescriptor() {
			return getSource().getReturnTypeDescriptor();
		}

	}

	TypeDescriptor getReturnTypeDescriptor();

}
