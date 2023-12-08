package io.basc.framework.orm;

import io.basc.framework.observe.register.ElementRegistry;
import io.basc.framework.observe.register.Registry;
import io.basc.framework.util.Registration;
import io.basc.framework.util.TypeRegistry;
import io.basc.framework.util.element.Elements;

public class EntityRepositoryRegistry<T extends EntityOperations> extends TypeRegistry<Object, T> {
	private static Registry<EntityRepositoryRegistry<?>> repositoryRegistry = new ElementRegistry<>();

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
