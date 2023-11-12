package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;

/**
 * 对一个实例方法的定义
 */
public interface Method extends Executable {
	/**
	 * 名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 执行实例的类型描述
	 * 
	 * @return
	 */
	TypeDescriptor getTargetTypeDescriptor();

	Object execute(Object target, Elements<Object> args) throws Throwable;
}
