package io.basc.framework.core.execution;

import lombok.NonNull;

/**
 * 函数的定义
 * 
 * @author wcnnkh
 *
 */
public interface Function extends Executable, Executor {
	@FunctionalInterface
	public static interface FunctionWrapper<W extends Function>
			extends Function, ExecutableWrapper<W>, ExecutorWrapper<W> {
		@Override
		default Object execute(@NonNull Object... args) throws Throwable {
			return getSource().execute(args);
		}

		@Override
		default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
			return getSource().execute(parameterTypes, args);
		}
	}

	default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
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
	Object execute(@NonNull Object... args) throws Throwable;
}
