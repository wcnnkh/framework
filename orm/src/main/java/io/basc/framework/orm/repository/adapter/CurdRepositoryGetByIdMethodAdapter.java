package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.RepositoryTemplate;

import java.lang.reflect.Method;

public final class CurdRepositoryGetByIdMethodAdapter extends
		CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName,
			Class<?>[] parameterTypes) {
		return methodName.equals("getById");
	}

	@Override
	protected Object intercept(RepositoryTemplate repository,
			MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName)
			throws Throwable {
		if (args.length == 2) {
			return repository.getById((TypeDescriptor) args[0], entityClass,
					(Object[]) args[1]);
		}
		return repository.getById(resultsTypeDescriptor, entityClass,
				(Object[]) args[0]);
	}

}
