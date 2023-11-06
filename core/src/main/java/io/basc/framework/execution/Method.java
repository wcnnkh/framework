package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;

/**
 * 方法
 * 
 * @author wcnnkh
 *
 */
public interface Method extends Executable {

	TypeDescriptor getTargetTypeDescriptor();

	/**
	 * 名称
	 */
	String getName();

	default Object execute(Object target) throws Throwable {
		return execute(target, new Object[0]);
	}

	/**
	 * 执行
	 * 
	 * @param target
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	Object execute(Object target, Object[] args) throws Throwable;
}
