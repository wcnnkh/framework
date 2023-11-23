package io.basc.framework.execution;

import java.util.function.Predicate;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.Processor;

public interface Invocation
		extends Executable, Predicate<Elements<Parameter>>, Processor<Elements<Parameter>, Object, Throwable> {
	/**
	 * 获取执行参数
	 * 
	 * @see #canExecuted()
	 * 
	 * @return
	 */
	Elements<Parameter> getParameters();

	/**
	 * 设置执行参数
	 * 
	 * @see #execute(Elements)
	 * 
	 * @param parameters
	 */
	void setParameters(Elements<Parameter> parameters);

	@Override
	default boolean canExecuted() {
		return test(getParameters());
	}

	@Override
	default Object execute() throws Throwable {
		return process(getParameters());
	}

	@Override
	boolean test(Elements<Parameter> parameters);

	@Override
	Object process(Elements<Parameter> parameters) throws Throwable;
}
