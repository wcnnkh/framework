package io.basc.framework.execution.aop;

import io.basc.framework.execution.Constructor;

public interface Proxy extends Constructor {

	@Override
	Object execute();

	@Override
	Object execute(Class<?>[] types, Object[] args);
}
