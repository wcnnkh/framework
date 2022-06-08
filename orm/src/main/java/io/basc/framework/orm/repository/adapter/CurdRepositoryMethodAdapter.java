package io.basc.framework.orm.repository.adapter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.Curd;
import io.basc.framework.orm.repository.RepositoryTemplate;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public abstract class CurdRepositoryMethodAdapter implements
		RepositoryMethodAdapter {

	@Override
	public final boolean test(MethodInvoker invoker) {
		if (Curd.class != invoker.getMethod().getDeclaringClass()) {
			return false;
		}

		Method method = invoker.getMethod();
		return test(method, method.getName(), method.getParameterTypes());
	}

	protected abstract boolean test(Method method, String methodName,
			Class<?>[] parameterTypes);

	@Override
	public final Object intercept(RepositoryTemplate template,
			MethodInvoker invoker, Object[] args) throws Throwable {
		TypeDescriptor resultsTypeDescriptor = TypeDescriptor
				.forMethodReturnType(invoker.getMethod());
		TypeDescriptor entityTypeDescriptor = null;
		Type[] types = invoker.getSourceClass().getGenericInterfaces();
		for (Type type : types) {
			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(type);
			if (typeDescriptor.getType() == Curd.class) {
				entityTypeDescriptor = typeDescriptor.getGeneric(0);
				break;
			}
		}

		if (entityTypeDescriptor == null) {
			throw new IllegalAccessError(invoker.toString());
		}

		return intercept(template, invoker, args,
				entityTypeDescriptor.getType(), resultsTypeDescriptor, invoker
						.getMethod().getName());
	}

	protected abstract Object intercept(RepositoryTemplate template,
			MethodInvoker invoker, Object[] args, Class<?> entityClass,
			TypeDescriptor resultsTypeDescriptor, String methodName)
			throws Throwable;
}
