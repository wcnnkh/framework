package run.soeasy.framework.core.execution;

import lombok.NonNull;
import run.soeasy.framework.core.param.Parameters;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.ObjectUtils;

public interface Executor extends Executed {
	@FunctionalInterface
	public static interface ExecutorWrapper<W extends Executor> extends Executor, ExecutedWrapper<W> {

		@Override
		default boolean canExecuted() {
			return getSource().canExecuted();
		}

		@Override
		default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
			return getSource().execute(parameterTypes, args);
		}

		@Override
		default Object execute(@NonNull Parameters parameters) throws Throwable {
			return getSource().execute(parameters);
		}
	}

	default Object execute() throws Throwable {
		return execute(ClassUtils.emptyArray(), ObjectUtils.EMPTY_ARRAY);
	}

	Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable;

	default Object execute(@NonNull Parameters parameters) throws Throwable {
		if (!parameters.isValidated()) {
			throw new IllegalArgumentException();
		}
		return execute(parameters.getTypes(), parameters.getArgs());
	}
}