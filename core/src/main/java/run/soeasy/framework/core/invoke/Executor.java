package run.soeasy.framework.core.invoke;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.type.ClassUtils;

public interface Executor extends Executable {
	@FunctionalInterface
	public static interface ExecutorWrapper<W extends Executor> extends Executor, ExecutableWrapper<W> {

		@Override
		default boolean canExecuted() {
			return getSource().canExecuted();
		}

		@Override
		default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
			return getSource().execute(parameterTypes, args);
		}

		@Override
		default Object execute(@NonNull ParameterSource parameters) throws Throwable {
			return getSource().execute(parameters);
		}
	}

	default Object execute() throws Throwable {
		return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;

	default Object execute(@NonNull ParameterSource parameters) throws Throwable {
		if (!parameters.isValidated()) {
			throw new IllegalArgumentException();
		}
		return execute(parameters.getTypes(), parameters.getArgs());
	}
}