package io.basc.framework.orm.repository.adapter;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.repository.Repository;

import java.lang.reflect.Method;

public class RepositoryMethodAdapterRegistry extends
		ConfigurableServices<RepositoryMethodAdapter> implements
		RepositoryMethodAdapter {

	public RepositoryMethodAdapterRegistry() {
		super(RepositoryMethodAdapter.class);
		addService(new CurdRepositoryDeleteAdapter());
		addService(new CurdRepositoryDeleteAllAdapter());
		addService(new CurdRepositoryDeleteByIdAdapter());
		addService(new CurdRepositoryGetByIdMethodAdapter());
		addService(new CurdRepositoryGetInIdsMethodAdapter());
		addService(new CurdRepositoryIsPresentAnyMethodAdapter());
		addService(new CurdRepositoryIsPresentMethodAdapter());
		addService(new CurdRepositoryPagingQueryMethodAdapter());
		addService(new CurdRepositoryQueryAllMethodAdapter());
		addService(new CurdRepositoryQueryListMethodAdapter());
		addService(new CurdRepositoryQueryMethodAdapter());
		addService(new CurdRepositorySaveMethodAdapter());
		addService(new CurdRepositoryUpdateAllMethodAdapter());
		addService(new CurdRepositoryUpdateMethodAdapter());
		addService(new CurdRepositorySaveIfAbsentAdapter());
		setAfterService(new CustomCurdRepositoryMethodAdapter());
	}

	@Override
	public boolean test(Method method) {
		for (RepositoryMethodAdapter adapter : this) {
			if (adapter.test(method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object intercept(Repository repository, MethodInvoker invoker,
			Object[] args) throws Throwable {
		for (RepositoryMethodAdapter adapter : this) {
			if (adapter.test(invoker.getMethod())) {
				return adapter.intercept(repository, invoker, args);
			}
		}
		throw new NotSupportedException(invoker.toString());
	}
}
