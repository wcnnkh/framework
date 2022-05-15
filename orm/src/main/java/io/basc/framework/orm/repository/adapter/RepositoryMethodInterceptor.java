package io.basc.framework.orm.repository.adapter;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.orm.repository.CurdRepository;
import io.basc.framework.orm.repository.Repository;
import io.basc.framework.util.Assert;

import java.lang.reflect.Modifier;

public class RepositoryMethodInterceptor extends
		RepositoryMethodAdapterRegistry implements MethodInterceptor {
	private final Repository repository;

	public RepositoryMethodInterceptor(Repository repository) {
		Assert.requiredArgument(repository != null, "repository");
		this.repository = repository;
		setAfterService(new CustomCurdRepositoryMethodAdapter());
	}

	public Repository getRepository() {
		return repository;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args)
			throws Throwable {
		if (!(Modifier.isAbstract(invoker.getMethod().getModifiers()) || Modifier
				.isInterface(invoker.getMethod().getModifiers()))) {
			return invoker.invoke(args);
		}

		if (!CurdRepository.class.isAssignableFrom(invoker.getSourceClass())) {
			return invoker.invoke(args);
		}

		return intercept(repository, invoker, args);
	}
}
