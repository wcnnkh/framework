package io.basc.framework.execution;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;

/**
 * 函数的定义
 * 
 * @author wcnnkh
 *
 */
public interface Function extends Executable, Executor {
	default Object execute(Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args)
			throws Throwable {
		if (!canExecuted(parameterTypes)) {
			throw new IllegalArgumentException("Parameter type mismatch");
		}

		return execute(args);
	}

	/**
	 * 执行
	 * 
	 * @param args
	 * @return
	 */
	Object execute(Elements<? extends Object> args) throws Throwable;

	default Object execute(Parameters parameters) throws Throwable {
		return execute(parameters, (results) -> execute(results.getParameters()));
	}

}
