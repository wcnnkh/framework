package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.orm.repository.RepositoryTemplate;

import java.lang.reflect.Method;

public final class CurdRepositoryQueryMethodAdapter extends CurdRepositoryMethodAdapter {

	@Override
	protected boolean test(Method method, String methodName, Class<?>[] parameterTypes) {
		return methodName.equals("query");
	}

	@Override
	protected Object intercept(RepositoryTemplate template, MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName) throws Throwable {
		if (args.length == 3) {
			PageRequest pageRequest = (PageRequest) args[2];
			return template.query((TypeDescriptor) args[0], entityClass, args[1]).jumpTo(pageRequest.getStart(),
					pageRequest.getPageSize());
		}

		PageRequest pageRequest = (PageRequest) args[1];
		return template.query(resultsTypeDescriptor, entityClass, args[0]).jumpTo(pageRequest.getStart(),
				pageRequest.getPageSize());
	}
}
