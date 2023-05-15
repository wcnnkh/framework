package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public abstract class AbstractExecutors implements Executors {

	@Override
	public boolean isExecutable() {
		return getElements().anyMatch((e) -> e.isExecutable());
	}

	@Override
	public Object execute() throws ExecutionException {
		for (Executor executor : getElements()) {
			if (executor.isExecutable()) {
				return executor.execute();
			}
		}
		throw new UnsupportedOperationException("execute()");
	}

	@Override
	public boolean isExecutable(Elements<? extends TypeDescriptor> types) {
		return getElements().anyMatch((e) -> e.isExecutable(types));
	}

	@Override
	public Object execute(Elements<? extends Value> args) throws ExecutionException {
		Elements<TypeDescriptor> types = args.map((e) -> e.getTypeDescriptor());
		for (Executor executor : getElements()) {
			if (executor.isExecutable(types)) {
				return executor.execute(args);
			}
		}
		throw new UnsupportedOperationException("execute(" + args.toString() + ")");
	}
}
