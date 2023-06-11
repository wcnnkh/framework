package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;

/**
 * 定义一个执行器
 * 
 * @author wcnnkh
 *
 */
public interface Executor {
	default boolean isExecuted() {
		return isExecuted(Elements.empty());
	}

	default Object execute() throws Throwable {
		return execute(Elements.empty(), Elements.empty());
	}

	boolean isExecuted(Elements<? extends TypeDescriptor> types);

	Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args) throws Throwable;
}
