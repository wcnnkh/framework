package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ParameterMatchingResults;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.Named;
import io.basc.framework.util.element.Elements;

/**
 * 对一个实例方法的定义
 */
public interface Method extends Executor, Named {

	default Object execute(Object target) throws Throwable {
		return execute(target, getParameters());
	}

	Object execute(Object target, Elements<Object> args) throws Throwable;

	default Object execute(Object target, Parameters parameters) throws Throwable {
		ParameterMatchingResults results = parameters.apply(getParameterDescriptors());
		if (!results.isSuccessful()) {
			throw new IllegalArgumentException("Parameter mismatch");
		}

		Elements<Object> args = results.getParameters();
		return execute(target, args);
	}

	Object getTarget();

	/**
	 * 执行实例的类型描述
	 * 
	 * @return
	 */
	TypeDescriptor getTargetTypeDescriptor();

	void setTarget(Object target);
}
