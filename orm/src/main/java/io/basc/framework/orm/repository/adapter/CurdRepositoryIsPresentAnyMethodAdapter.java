package io.basc.framework.orm.repository.adapter;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.RepositoryTemplate;

public final class CurdRepositoryIsPresentAnyMethodAdapter extends CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName, Class<?>[] parameterTypes) {
		return methodName.equals("isPresentAny");
	}

	@Override
	protected Object intercept(RepositoryTemplate template, MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName) throws Throwable {
		return !template.query(entityClass, args[0]).isEmpty();
	}
}
