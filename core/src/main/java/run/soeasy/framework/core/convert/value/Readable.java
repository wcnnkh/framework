package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface Readable {
	@FunctionalInterface
	public static interface ReadableWrapper<W extends Readable> extends Readable, Wrapper<W> {
		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}

	}

	TypeDescriptor getTypeDescriptor();
}
