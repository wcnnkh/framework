package io.basc.framework.core.convert;

import io.basc.framework.util.Wrapper;

@FunctionalInterface
public interface ValueDescriptor {
	@FunctionalInterface
	public static interface ValueDescriptorWrapper<W extends ValueDescriptor> extends ValueDescriptor, Wrapper<W> {
		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}
	}

	TypeDescriptor getTypeDescriptor();
}
