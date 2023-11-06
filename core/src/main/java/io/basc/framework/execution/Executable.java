package io.basc.framework.execution;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

/**
 * 可执行的
 * 
 * @author wcnnkh
 *
 */
public interface Executable extends Constructable {
	/**
	 * 执行需要的参数类型
	 * 
	 * @return
	 */
	ParameterDescriptor[] getParameterDescriptors();

	default boolean isExecuted(Class<?>[] types) {
		return Elements.forArray(getParameterDescriptors()).map((e) -> e.getTypeDescriptor().getType())
				.equals(Elements.forArray(types), Class::isAssignableFrom);
	}
}
