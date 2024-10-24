package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.RepositoryTemplate;

import java.lang.reflect.Method;

public final class CurdRepositoryDeleteByIdAdapter extends
		CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName,
			Class<?>[] parameterTypes) {
		return methodName.equals("deleteById");
	}

	@Override
	protected Object intercept(RepositoryTemplate repository,
			MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName)
			throws Throwable {
		return repository.deleteById(entityClass, args);
	}
}
