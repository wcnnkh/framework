package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.convert.TypeDescriptor;

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
