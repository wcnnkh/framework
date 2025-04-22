package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface Readable {
	@FunctionalInterface
	public static interface ReadableWrapper<W extends Readable> extends Readable, Wrapper<W> {
		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}

		@Override
		default boolean isPresent() {
			return getSource().isPresent();
		}
	}

	default boolean isPresent() {
		return true;
	}

	TypeDescriptor getTypeDescriptor();
}
