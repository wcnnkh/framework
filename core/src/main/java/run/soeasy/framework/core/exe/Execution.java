package run.soeasy.framework.core.exe;

import lombok.NonNull;
import run.soeasy.framework.core.param.Parameters;

/**
 * 函数的定义
 * 
 * @author wcnnkh
 *
 */
public interface Execution extends Executable, Executor {
	@FunctionalInterface
	public static interface FunctionWrapper<W extends Execution>
			extends Execution, ExecutableWrapper<W>, ExecutorWrapper<W> {
		@Override
		default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
			return getSource().execute(parameterTypes, args);
		}

		@Override
		default Object execute(@NonNull Object... args) throws Throwable {
			return getSource().execute(args);
		}

		@Override
		default Object execute(@NonNull Parameters parameters) throws Throwable {
			return getSource().execute(parameters);
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
		default Parameters getDefaultParameters() {
			return getSource().getDefaultParameters();
		}

		@Override
		default void setDefaultParameters(Parameters parameters) {
			getSource().setDefaultParameters(parameters);
		}
	}

	public static class RenamedFunction<W extends Execution> extends RenamedExecutable<W>
			implements FunctionWrapper<W> {

		public RenamedFunction(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public Execution rename(String name) {
			return new RenamedFunction<>(name, getSource());
		}
	}

	Parameters getDefaultParameters();

	void setDefaultParameters(Parameters parameters);

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
	default Object execute(@NonNull Parameters parameters) throws Throwable {
		Parameters completed = parameters.isValidated() ? parameters : parameters.reconstruct(this);
		return Executor.super.execute(completed);
	}

	@Override
	default Execution rename(String name) {
		return new RenamedFunction<>(name, this);
	}
}
