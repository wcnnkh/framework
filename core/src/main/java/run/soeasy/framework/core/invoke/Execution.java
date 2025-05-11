package run.soeasy.framework.core.invoke;

import lombok.NonNull;

/**
 * 函数的定义
 * 
 * @author wcnnkh
 *
 */
public interface Execution extends ExecutableDescriptor, Executor {
	@FunctionalInterface
	public static interface ExecutionWrapper<W extends Execution>
			extends Execution, ExecutableDescriptorWrapper<W>, ExecutorWrapper<W> {
		@Override
		default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
			return getSource().execute(parameterTypes, args);
		}

		@Override
		default Object execute(@NonNull Object... args) throws Throwable {
			return getSource().execute(args);
		}

		@Override
		default Execution rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedExecution<W extends Execution> extends RenamedExecutable<W>
			implements ExecutionWrapper<W> {

		public RenamedExecution(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public Execution rename(String name) {
			return new RenamedExecution<>(name, getSource());
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

	@Override
	default Execution rename(String name) {
		return new RenamedExecution<>(name, this);
	}
}
