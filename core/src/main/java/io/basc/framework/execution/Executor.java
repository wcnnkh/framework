package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;

/**
 * 执行器
 * 
 * @author wcnnkh
 *
 */
public interface Executor extends Executable, Constructor {
	/**
	 * 执行
	 * 
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	Object execute(Elements<? extends Object> args) throws Throwable;

	default Object execute() throws Throwable {
		return execute(Elements.empty());
	}

	@Override
	default Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args) throws Throwable {
		// TODO 是否需要根据类型重新整理参数顺序
		return execute(args);
	}
}
