package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.RepositoryTemplate;

import java.lang.reflect.Method;

public final class CurdRepositoryQueryAllMethodAdapter extends
		CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName,
			Class<?>[] parameterTypes) {
		return methodName.equals("queryAll");
	}

	@Override
	protected Object intercept(RepositoryTemplate repository,
			MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName)
			throws Throwable {
		if (args != null && args.length == 1) {
			return repository.queryAll((TypeDescriptor) args[0], entityClass,
					args[1]);
		}
		return repository.queryAll(resultsTypeDescriptor, entityClass);
	}
}
