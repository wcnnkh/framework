package io.basc.framework.orm.repository.adapter;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.orm.repository.RepositoryTemplate;

public class RepositoryMethodAdapterRegistry extends ConfigurableServices<RepositoryMethodAdapter>
		implements RepositoryMethodAdapter {

	public RepositoryMethodAdapterRegistry() {
		super(RepositoryMethodAdapter.class);
		registerService(new CurdRepositoryDeleteAdapter());
		registerService(new CurdRepositoryDeleteAllAdapter());
		registerService(new CurdRepositoryDeleteByIdAdapter());
		registerService(new CurdRepositoryGetByIdMethodAdapter());
		registerService(new CurdRepositoryGetInIdsMethodAdapter());
		registerService(new CurdRepositoryIsPresentAnyMethodAdapter());
		registerService(new CurdRepositoryIsPresentMethodAdapter());
		registerService(new CurdRepositoryPagingQueryMethodAdapter());
		registerService(new CurdRepositoryQueryAllMethodAdapter());
		registerService(new CurdRepositoryQueryListMethodAdapter());
		registerService(new CurdRepositoryQueryMethodAdapter());
		registerService(new CurdRepositorySaveMethodAdapter());
		registerService(new CurdRepositoryUpdateAllMethodAdapter());
		registerService(new CurdRepositoryUpdateMethodAdapter());
		registerService(new CurdRepositorySaveIfAbsentAdapter());
		setLast(new CustomRepositoryMethodAdapter());
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
