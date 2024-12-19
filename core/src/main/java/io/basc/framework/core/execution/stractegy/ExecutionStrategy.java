package io.basc.framework.core.execution.stractegy;

import io.basc.framework.core.convert.transform.Parameters;
import io.basc.framework.core.execution.Executor;
import io.basc.framework.util.Elements;

public interface ExecutionStrategy<T extends Executor> {
	Object execute(Elements<T> executors, Parameters parameters);
}
