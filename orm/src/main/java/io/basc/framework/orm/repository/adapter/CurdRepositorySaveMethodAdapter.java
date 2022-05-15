package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.Repository;

import java.lang.reflect.Method;

public final class CurdRepositorySaveMethodAdapter extends
		CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName,
			Class<?>[] parameterTypes) {
		return methodName.equals("save");
	}

	@Override
	protected Object intercept(Repository repository, MethodInvoker invoker,
			Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName)
			throws Throwable {
		repository.save(entityClass, args[0]);
		return null;
	}
}
