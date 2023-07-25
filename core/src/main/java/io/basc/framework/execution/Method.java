package io.basc.framework.execution;

import io.basc.framework.util.element.Elements;

/**
 * 方法
 * 
 * @author wcnnkh
 *
 */
public interface Method extends Executable {
	default Object execute(Object target) throws Throwable {
		return execute(target, Elements.empty());
	}

	/**
	 * 执行
	 * 
	 * @param target
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	Object execute(Object target, Elements<? extends Object> args) throws Throwable;
}
