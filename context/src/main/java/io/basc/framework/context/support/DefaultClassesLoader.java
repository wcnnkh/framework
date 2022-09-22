package io.basc.framework.context.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ConfigurableClassesLoader;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;
import io.basc.framework.util.DuplicateRemovalIterator;
import io.basc.framework.util.MultiIterator;

public class DefaultClassesLoader implements ConfigurableClassesLoader, Configurable {
	private final Set<Class<?>> defaultClasses = new LinkedHashSet<Class<?>>();
	private Accept<Class<?>> accept;
	private final ConfigurableServices<ClassesLoader> serviceList = new ConfigurableServices<>(ClassesLoader.class);

	public DefaultClassesLoader() {
	}

	public DefaultClassesLoader(@Nullable Accept<Class<?>> accept) {
		this.accept = accept;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		serviceList.configure(serviceLoaderFactory);
	}

	public void add(Class<?> clazz) {
		if (accept != null && !accept.accept(clazz)) {
			return;
		}
		defaultClasses.add(clazz);
	}

	public void add(ClassesLoader classesLoader) {
		serviceList.addService(accept == null ? classesLoader : new AcceptClassesLoader(classesLoader, accept));
	}

	public Set<Class<?>> getDefaultClasses() {
		return defaultClasses;
	}

	public void reload() {
		for (ClassesLoader classesLoader : serviceList) {
			classesLoader.reload();
		}
	}

	public Iterator<Class<?>> iterator() {
		List<Iterator<Class<?>>> iterators = new ArrayList<Iterator<Class<?>>>();
		iterators.add(defaultClasses.iterator());

		for (ClassesLoader classesLoader : serviceList) {
			iterators.add(classesLoader.iterator());
		}

		Iterator<Class<?>> iterator = new MultiIterator<Class<?>>(iterators);
		return new DuplicateRemovalIterator<Class<?>>(iterator);
	}
}
