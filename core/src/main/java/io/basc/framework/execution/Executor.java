package io.basc.framework.execution;

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
	Object execute(Object[] args) throws Throwable;

	default Object execute() throws Throwable {
		return execute(new Object[0]);
	}

	@Override
	default Object execute(Class<?>[] types, Object[] args) throws Throwable {
		// TODO 是否需要根据类型重新整理参数顺序
		return execute(args);
	}
}
