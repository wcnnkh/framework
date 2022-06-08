package io.basc.framework.orm.repository.adapter;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.RepositoryTemplate;

public interface RepositoryMethodAdapter {
	boolean test(MethodInvoker invoker);

	Object intercept(RepositoryTemplate template, MethodInvoker invoker,
			Object[] args) throws Throwable;
}
