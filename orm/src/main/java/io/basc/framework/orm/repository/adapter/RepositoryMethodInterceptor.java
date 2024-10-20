package io.basc.framework.orm.repository.adapter;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.Curd;
import io.basc.framework.orm.repository.RepositoryTemplate;
import io.basc.framework.util.Assert;

import java.lang.reflect.Modifier;

public class RepositoryMethodInterceptor extends
		RepositoryMethodAdapterRegistry implements MethodInterceptor {
	private final RepositoryTemplate template;

	public RepositoryMethodInterceptor(RepositoryTemplate template) {
		Assert.requiredArgument(template != null, "template");
		this.template = template;
		setAfterService(new CustomRepositoryMethodAdapter());
	}

	public final RepositoryTemplate getTemplate() {
		return template;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args)
			throws Throwable {
		if (!(Modifier.isAbstract(invoker.getMethod().getModifiers()) || Modifier
				.isInterface(invoker.getMethod().getModifiers()))) {
			return invoker.invoke(args);
		}

		if (!Curd.class.isAssignableFrom(invoker.getSourceClass())) {
			return invoker.invoke(args);
		}

		return intercept(template, invoker, args);
	}
}
