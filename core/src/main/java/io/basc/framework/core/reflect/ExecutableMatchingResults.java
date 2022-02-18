package io.basc.framework.core.reflect;

import java.lang.reflect.Executable;

import io.basc.framework.util.Assert;

public class ExecutableMatchingResults<E extends Executable> {
	private final E executable;
	private final Object[] params;
	private final int matchingResultes;

	public ExecutableMatchingResults(E executable, Object[] params, int matchingResultes) {
		Assert.requiredArgument(executable != null, "executable");
		Assert.requiredArgument(params != null, "params");
		this.executable = executable;
		this.params = params;
		this.matchingResultes = matchingResultes;
	}

	public E getExecutable() {
		return executable;
	}

	public Object[] getUnsafeParams() {
		return params;
	}

	public Object[] getParams() {
		return params.clone();
	}

	public int getMatchingResultes() {
		return matchingResultes;
	}
}
