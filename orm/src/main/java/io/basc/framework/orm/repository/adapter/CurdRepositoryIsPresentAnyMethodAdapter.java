package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.Repository;

import java.lang.reflect.Method;

public final class CurdRepositoryIsPresentAnyMethodAdapter extends
		CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName,
			Class<?>[] parameterTypes) {
		return methodName.equals("isPresentAny");
	}

	@Override
	protected Object intercept(Repository repository, MethodInvoker invoker,
			Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName)
			throws Throwable {
		return repository.isPresentAny(entityClass, args[0]);
	}
}
