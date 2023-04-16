package io.basc.framework.orm.repository.adapter;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.orm.repository.RepositoryTemplate;

public class RepositoryMethodAdapterRegistry extends ConfigurableServices<RepositoryMethodAdapter>
		implements RepositoryMethodAdapter {

	public RepositoryMethodAdapterRegistry() {
		super(RepositoryMethodAdapter.class);
		register(new CurdRepositoryDeleteAdapter());
		register(new CurdRepositoryDeleteAllAdapter());
		register(new CurdRepositoryDeleteByIdAdapter());
		register(new CurdRepositoryGetByIdMethodAdapter());
		register(new CurdRepositoryGetInIdsMethodAdapter());
		register(new CurdRepositoryIsPresentAnyMethodAdapter());
		register(new CurdRepositoryIsPresentMethodAdapter());
		register(new CurdRepositoryPagingQueryMethodAdapter());
		register(new CurdRepositoryQueryAllMethodAdapter());
		register(new CurdRepositoryQueryListMethodAdapter());
		register(new CurdRepositoryQueryMethodAdapter());
		register(new CurdRepositorySaveMethodAdapter());
		register(new CurdRepositoryUpdateAllMethodAdapter());
		register(new CurdRepositoryUpdateMethodAdapter());
		register(new CurdRepositorySaveIfAbsentAdapter());
		registerLast(new CustomRepositoryMethodAdapter());
	}

	@Override
	public boolean test(MethodInvoker invoker) {
		for (RepositoryMethodAdapter adapter : this) {
			if (adapter.test(invoker)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object intercept(RepositoryTemplate repository, MethodInvoker invoker, Object[] args) throws Throwable {
		for (RepositoryMethodAdapter adapter : this) {
			if (adapter.test(invoker)) {
				return adapter.intercept(repository, invoker, args);
			}
		}
		throw new UnsupportedException(invoker.toString());
	}
}
