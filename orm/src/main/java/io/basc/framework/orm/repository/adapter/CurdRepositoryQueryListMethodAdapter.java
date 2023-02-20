package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.RepositoryTemplate;

import java.lang.reflect.Method;

public final class CurdRepositoryQueryListMethodAdapter extends CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName, Class<?>[] parameterTypes) {
		return methodName.equals("queryList");
	}

	@Override
	protected Object intercept(RepositoryTemplate template, MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName) throws Throwable {
		if (args.length == 2) {
			return template.query((TypeDescriptor) args[0], entityClass, args[1]).toList();
		}
		return template.query(resultsTypeDescriptor, entityClass, args[0]).toList();
	}

}
