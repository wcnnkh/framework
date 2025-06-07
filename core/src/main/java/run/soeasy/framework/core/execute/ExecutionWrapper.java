package run.soeasy.framework.core.execute;

import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface ExecutionWrapper<W extends Execution> extends Execution, Wrapper<W> {
	@Override
	default Object[] getArguments() {
		return getSource().getArguments();
	}

	@Override
	default Object execute() throws Throwable {
		return getSource().execute();
	}

	@Override
	default ExecutableMetadata getMetadata() {
		return getSource().getMetadata();
	}
}