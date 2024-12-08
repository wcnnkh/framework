package io.basc.framework.core.execution.stractegy;

import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.annotation.MergedAnnotationsElements;
import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.core.convert.transform.Parameters;
import io.basc.framework.core.execution.Executor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Listable;
import lombok.NonNull;

public interface Executors<T extends Executor> extends Executor, Listable<T> {

	@Override
	default boolean canExecuted() {
		for (T service : getElements()) {
			if (service.canExecuted()) {
				return true;
			}
		}
		return false;
	}

	@Override
	default boolean canExecuted(Class<?>... parameterTypes) {
		for (T service : getElements()) {
			if (service.canExecuted(parameterTypes)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default boolean canExecuted(@NonNull Parameters parameters) {
		for (T service : getElements()) {
			if (service.canExecuted(parameters)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default ValueWrapper execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		for (T service : getElements()) {
			if (service.canExecuted(parameterTypes)) {
				Object value = service.execute(parameterTypes, args);
				return ValueWrapper.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException(parameterTypes.toString());
	}

	@Override
	default ValueWrapper execute() throws Throwable {
		for (T service : getElements()) {
			if (service.canExecuted()) {
				Object value = service.execute();
				return ValueWrapper.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException("");
	}

	@Override
	default ValueWrapper execute(@NonNull Parameters parameters) throws Throwable {
		for (T service : getElements()) {
			if (service.canExecuted()) {
				Object value = service.execute(parameters);
				return ValueWrapper.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException("");
	}

	@Override
	default MergedAnnotations getAnnotations() {
		return new MergedAnnotationsElements(getElements().map((e) -> e.getAnnotations()));
	}
}
