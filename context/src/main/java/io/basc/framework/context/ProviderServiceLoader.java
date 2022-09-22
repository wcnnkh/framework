package io.basc.framework.context;

import java.util.Iterator;

import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.support.ClassInstanceIterator;

public class ProviderServiceLoader<S> implements ServiceLoader<S> {
	private final ClassesLoader providers;
	private final InstanceFactory instanceFactory;

	public ProviderServiceLoader(ClassesLoader classesLoader, InstanceFactory instanceFactory,
			ContextResolver contextResolver, Class<S> serviceClass) {
		this(new ProviderClassesLoader(classesLoader, serviceClass, contextResolver), instanceFactory);
	}

	public ProviderServiceLoader(ClassesLoader providers, InstanceFactory instanceFactory) {
		this.providers = providers;
		this.instanceFactory = instanceFactory;
	}

	public void reload() {
		this.providers.reload();
	}

	public Iterator<S> iterator() {
		return new ClassInstanceIterator<S>(instanceFactory, providers.iterator());
	}
}
