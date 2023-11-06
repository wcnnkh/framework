package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;

/**
 * 可构造的
 * 
 * @author wcnnkh
 *
 */
public interface Constructable {
	/**
	 * 返回类型
	 * 
	 * @return
	 */
	TypeDescriptor getReturnTypeDescriptor();

	/**
	 * 是否可执行
	 * 
	 * @return
	 */
	default boolean isExecuted() {
		return isExecuted(new Class<?>[0]);
	}

	/**
	 * 是否可执行
	 * 
	 * @param types
	 * @return
	 */
	boolean isExecuted(Class<?>[] types);
}
