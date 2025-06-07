package run.soeasy.framework.core.execute;

import lombok.NonNull;

public interface InvodableElement extends ExecutableMetadata, InvokableTemplate {
	@Override
	default Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		if (!canExecuted(parameterTypes)) {
			throw new IllegalArgumentException("Parameter type mismatch");
		}

		return invoke(target, args);
	}

	Object invoke(Object target, @NonNull Object... args) throws Throwable;
}
