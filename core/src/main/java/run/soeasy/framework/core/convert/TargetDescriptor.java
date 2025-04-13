package run.soeasy.framework.core.convert;

import run.soeasy.framework.core.Wrapper;

@FunctionalInterface
public interface TargetDescriptor {
	public static interface TargetDescriptorWrapper<W extends TargetDescriptor> extends TargetDescriptor, Wrapper<W> {
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
	 * @return true表示set是不能插入空值
	 */
	default boolean isRequired() {
		return false;
	}
}
