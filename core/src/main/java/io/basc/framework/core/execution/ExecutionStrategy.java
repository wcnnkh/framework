package io.basc.framework.core.execution;

import java.util.Optional;

import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.annotation.MergedAnnotationsElements;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.spi.Services;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ExecutionStrategy<T extends Executor> extends Services<T> implements Executor {
	private Parameters parameters;
	@NonNull
	private final TypeDescriptor returnTypeDescriptor;

	@Override
	public boolean canExecuted() {
		Parameters parameters = getParameters();
		return parameters == null ? Executor.super.canExecuted(parameters) : canExecuted(parameters);
	}

	@Override
	public Object execute() throws Throwable {
		Parameters parameters = getParameters();
		return parameters == null ? Executor.super.execute(parameters) : execute(parameters);
	}

	@Override
	public Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		Optional<T> optional = filter((e) -> e.canExecuted(parameterTypes)).findFirst();
		if (!optional.isPresent()) {
			throw new IllegalArgumentException();
		}
		return optional.get().execute(parameterTypes, args);
	}

	@Override
	public boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		return anyMatch((e) -> e.canExecuted(parameterTypes));
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return new MergedAnnotationsElements(map((e) -> e.getAnnotations()));
	}
}
