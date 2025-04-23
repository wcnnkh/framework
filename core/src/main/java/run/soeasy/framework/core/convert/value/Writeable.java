package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface Writeable {
	public static interface WriteableWrapper<W extends Writeable> extends Writeable, Wrapper<W> {
		@Override
		default TypeDescriptor getRequiredTypeDescriptor() {
			return getSource().getRequiredTypeDescriptor();
		}

		@Override
		default boolean isRequired() {
			return getSource().isRequired();
		}
	}

	TypeDescriptor getRequiredTypeDescriptor();

	/**
	 * 是否是必需的
	 * 
	 * @return true表示不能插入空值
	 */
	default boolean isRequired() {
		return false;
	}
}
