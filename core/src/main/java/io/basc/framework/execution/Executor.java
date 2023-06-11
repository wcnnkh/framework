package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;

/**
 * 定义一个执行器
 * 
 * @author wcnnkh
 *
 */
public interface Executor {

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
	 * 返回类型
	 */
	TypeDescriptor getReturnType();

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
