package io.basc.framework.orm.repository.adapter;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.Repository;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public interface RepositoryMethodAdapter extends Predicate<Method> {
	boolean test(Method method);

	Object intercept(Repository repository, MethodInvoker invoker, Object[] args)
			throws Throwable;
}
