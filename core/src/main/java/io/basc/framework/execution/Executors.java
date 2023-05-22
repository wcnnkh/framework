package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;

public interface Executors extends Executable {
	default boolean isExecuted() {
		return isExecuted(Elements.empty());
	}

	default Object execute() throws ExecutionException {
		return execute(Elements.empty(), Elements.empty());
	}

	default boolean isExecuted(Elements<? extends TypeDescriptor> types) {
		for (Executor executor : getExecutors()) {
			if (executor.getParameterDescriptors().equals(types,
					(param, type) -> type.isAssignableTo(param.getTypeDescriptor()))) {
				return true;
			}
		}
		return false;
	}

	default Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args) {
		for (Executor executor : getExecutors()) {
			if (executor.getParameterDescriptors().equals(types,
					(param, type) -> type.isAssignableTo(param.getTypeDescriptor()))) {
				return executor.execute(args);
			}
		}
		return false;
	}
}
