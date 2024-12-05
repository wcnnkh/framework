package io.basc.framework.core.execution;

import io.basc.framework.core.convert.transform.Parameters;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;
import lombok.NonNull;

public interface Executor extends Executed {
	@FunctionalInterface
	public static interface ExecutorWrapper<W extends Executor> extends Executor, ExecutedWrapper<W> {

		@Override
		default Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
			return getSource().execute(parameterTypes, args);
		}

		@Override
		default boolean canExecuted() {
			return getSource().canExecuted();
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
		return execute(parameters.getTypes(), parameters.getArgs());
	}
}