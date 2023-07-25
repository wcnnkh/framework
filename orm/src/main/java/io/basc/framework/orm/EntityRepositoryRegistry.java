package io.basc.framework.orm;

import io.basc.framework.util.TypeRegistry;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.registry.DefaultElementRegistry;
import io.basc.framework.util.registry.ElementRegistry;
import io.basc.framework.util.registry.Registration;

public class EntityRepositoryRegistry<T extends EntityOperations> extends TypeRegistry<Object, T> {
	private static ElementRegistry<EntityRepositoryRegistry<?>> repositoryRegistry = new DefaultElementRegistry<>();

	/**
	 * 维护全局所有的注册
	 * 
	 * @return
	 */
	public static Elements<EntityRepositoryRegistry<?>> all() {
		return repositoryRegistry.getElements();
	}

	private Registration registration;

	public EntityRepositoryRegistry() {
		this.registration = repositoryRegistry.register(this);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			registration.unregister();
		} finally {
			super.finalize();
		}
	}
}
