package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.TypeDescriptor;

public interface TargetDescriptor {
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
