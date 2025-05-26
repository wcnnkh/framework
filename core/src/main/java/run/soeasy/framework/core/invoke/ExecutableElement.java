package run.soeasy.framework.core.invoke;

import lombok.NonNull;

public interface ExecutableElement extends ExecutableMetadata, ExecutableTemplate {

	@Override
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
