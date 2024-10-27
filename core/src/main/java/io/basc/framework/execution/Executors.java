package io.basc.framework.execution;

import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.annotation.MergedAnnotationsElements;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;

public interface Executors<T extends Executor> extends Executor, ServiceLoader<T> {

	@Override
	default boolean canExecuted() {
		for (T service : getServices()) {
			if (service.canExecuted()) {
				return true;
			}
		}
		return false;
	}

	@Override
	default boolean canExecuted(Elements<? extends Class<?>> parameterTypes) {
		for (T service : getServices()) {
			if (service.canExecuted(parameterTypes)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default boolean canExecuted(Parameters parameters) {
		for (T service : getServices()) {
			if (service.canExecuted(parameters)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default ObjectValue execute(Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args)
			throws Throwable {
		for (T service : getServices()) {
			if (service.canExecuted(parameterTypes)) {
				Object value = service.execute(parameterTypes, args);
				return ObjectValue.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException(parameterTypes.toString());
	}

	@Override
	default ObjectValue execute() throws Throwable {
		for (T service : getServices()) {
			if (service.canExecuted()) {
				Object value = service.execute();
				return ObjectValue.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException("");
	}

	@Override
	default ObjectValue execute(Parameters parameters) throws Throwable {
		for (T service : getServices()) {
			if (service.canExecuted()) {
				Object value = service.execute(parameters);
				return ObjectValue.of(value, service.getReturnTypeDescriptor());
			}
		}
		throw new UnsupportedException("");
	}

	@Override
	default MergedAnnotations getAnnotations() {
		return new MergedAnnotationsElements(getServices().map((e) -> e.getAnnotations()));
	}
}
