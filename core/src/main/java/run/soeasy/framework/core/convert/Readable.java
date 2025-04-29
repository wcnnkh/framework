package run.soeasy.framework.core.convert;

import run.soeasy.framework.core.Wrapper;

public interface Readable {
	@FunctionalInterface
	public static interface ReadableWrapper<W extends Readable> extends Readable, Wrapper<W> {
		@Override
		default TypeDescriptor getReturnTypeDescriptor() {
			return getSource().getReturnTypeDescriptor();
		}

	}

	TypeDescriptor getReturnTypeDescriptor();
	
}
