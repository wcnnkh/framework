package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;

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
		return isExecuted(Elements.empty());
	}

	/**
	 * 是否可执行
	 * 
	 * @param types
	 * @return
	 */
	boolean isExecuted(Elements<? extends TypeDescriptor> types);
}
