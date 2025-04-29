package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.transform.mapping.ParameterSource;

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

		@Override
		default boolean canExecuted() {
			return getSource().canExecuted();
		}

		@Override
		default Object execute() throws Throwable {
			return getSource().execute();
		}

		@Override
		default ParameterSource getDefaultParameters() {
			return getSource().getDefaultParameters();
		}

		@Override
		default void setDefaultParameters(ParameterSource parameters) {
			getSource().setDefaultParameters(parameters);
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

	ParameterSource getDefaultParameters();

	void setDefaultParameters(ParameterSource parameters);

	@Override
	default boolean canExecuted() {
		return canExecuted(getDefaultParameters());
	}

	@Override
	default Object execute() throws Throwable {
		return execute(getDefaultParameters());
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
