package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;

/**
 * 定义一个可执行器
 * 
 * @author wcnnkh
 *
 */
public interface Executable {

	/**
	 * 执行器名称
	 */
	String getName();

	/**
	 * 执行器返回类型描述
	 */
	TypeDescriptor getTypeDescriptor();

	/**
	 * 执行需要的参数类型
	 * 
	 * @return
	 */
	Elements<? extends ParameterDescriptor> getParameterDescriptors();

	/**
	 * 执行
	 * 
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	Object execute(Elements<? extends Object> args) throws Throwable;
}
