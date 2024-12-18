package io.basc.framework.core.execution.stractegy;

import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.annotation.MergedAnnotationsElements;
import io.basc.framework.core.convert.Any;
import io.basc.framework.core.convert.transform.Parameters;
import io.basc.framework.core.convert.transform.Property;
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
	default Any execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		for (T service : getElements()) {
			if (service.canExecuted(parameterTypes)) {
				Object value = service.execute(parameterTypes, args);
				return Any.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException(parameterTypes.toString());
	}

	@Override
	default Any execute() throws Throwable {
		for (T service : getElements()) {
			if (service.canExecuted()) {
				Object value = service.execute();
				return Any.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException("");
	}

	@Override
	default Any execute(@NonNull Parameters parameters) throws Throwable {
		for (T service : getElements()) {
			if (service.canExecuted(parameters)) {
				Object value = service.execute(parameters);
				return Any.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException("");
	}

	@Override
	default MergedAnnotations getAnnotations() {
		return new MergedAnnotationsElements(getElements().map((e) -> e.getAnnotations()));
	}

	boolean canExecuted(Property ...properties);
}
