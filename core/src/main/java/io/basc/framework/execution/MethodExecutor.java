package io.basc.framework.execution;

import io.basc.framework.util.element.Elements;

/**
 * 一个方法的执行器
 */
public interface MethodExecutor extends Executor, Method {
	Object getTarget();

	void setTarget(Object target);

	/**
	 * 执行无参的调用
	 * 
	 * @param target
	 * @return
	 * @throws Throwable
	 */
	default Object execute(Object target) throws Throwable {
		return execute(target, Elements.empty());
	}
}
