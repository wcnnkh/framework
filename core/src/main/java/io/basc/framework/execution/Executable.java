package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;

/**
 * 一个可执行的定义
 */
public interface Executable {

	/**
	 * 返回类型
	 * 
	 * @return
	 */
	TypeDescriptor getReturnTypeDescriptor();

	/**
	 * 是否能执行
	 * 
	 * @return
	 */
	boolean canExecuted();

	/**
	 * 执行
	 * 
	 * @return
	 * @throws Throwable
	 */
	Object execute() throws Throwable;
}
