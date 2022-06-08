package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.RepositoryTemplate;
import io.basc.framework.util.ArrayUtils;

import java.lang.reflect.Method;

public final class CurdRepositoryDeleteAllAdapter extends
		CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName,
			Class<?>[] parameterTypes) {
		return methodName.equals("deleteAll");
	}

	@Override
	protected Object intercept(RepositoryTemplate repository,
			MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName)
			throws Throwable {
		if (ArrayUtils.isEmpty(args)) {
			return repository.deleteAll(entityClass);
		}
		return repository.deleteAll(entityClass, args[0]);
	}
}
