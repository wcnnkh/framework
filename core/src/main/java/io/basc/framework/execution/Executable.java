package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
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
	 * 来源
	 * 
	 * @return
	 */
	TypeDescriptor getSource();

	/**
	 * 名称
	 */
	String getName();

	/**
	 * 执行需要的参数类型
	 * 
	 * @return
	 */
	Elements<? extends ParameterDescriptor> getParameterDescriptors();

	default boolean isExecuted(Elements<? extends TypeDescriptor> types) {
		return getParameterDescriptors().map((e) -> e.getTypeDescriptor()).equals(types,
				TypeDescriptor::isAssignableTo);
	}
}
