package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;

/**
 * 可切换目标的执行器
 * 
 * @author wcnnkh
 *
 */
public interface SwitchableTargetExecutor extends Executor {
	Object getTarget();

	void setTarget(Object target);
}
