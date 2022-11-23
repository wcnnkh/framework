package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.RepositoryTemplate;

import java.lang.reflect.Method;
import java.util.List;

public final class CurdRepositoryGetInIdsMethodAdapter extends CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName, Class<?>[] parameterTypes) {
		return methodName.equals("getInIds");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object intercept(RepositoryTemplate template, MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName) throws Throwable {
		if (args.length == 3) {
			return template.getInIds((TypeDescriptor) args[0], entityClass, (List) args[1], (Object[]) args[2]);
		}
		return template.getInIds(resultsTypeDescriptor, entityClass, (List) args[0], (Object[]) args[1]);
	}
}
