package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ConfigurableClassesLoader;
import io.basc.framework.instance.Configurable;
import io.basc.framework.instance.ConfigurableServices;
import io.basc.framework.instance.ServiceLoaderFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;
import io.basc.framework.util.DuplicateRemovalIterator;
import io.basc.framework.util.MultiIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DefaultClassesLoader implements ConfigurableClassesLoader, Configurable {
	private final List<ClassesLoader> loaders = new LinkedList<ClassesLoader>();
	private final Set<Class<?>> defaultClasses = new LinkedHashSet<Class<?>>();
	private final Accept<Class<?>> accept;
	private final ConfigurableServices<ClassesLoader> serviceList = new ConfigurableServices<>(ClassesLoader.class);

	public DefaultClassesLoader() {
		this(null);
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
		loaders.add(accept == null ? classesLoader : new AcceptClassesLoader(classesLoader, accept, false));
	}

	public Set<Class<?>> getDefaultClasses() {
		return defaultClasses;
	}

	public void reload() {
		for (ClassesLoader classesLoader : loaders) {
			classesLoader.reload();
		}

		for (ClassesLoader classesLoader : serviceList) {
			classesLoader.reload();
		}
	}

	public Iterator<Class<?>> iterator() {
		List<Iterator<Class<?>>> iterators = new ArrayList<Iterator<Class<?>>>(loaders.size() + 1);
		iterators.add(defaultClasses.iterator());
		for (ClassesLoader classesLoader : loaders) {
			iterators.add(classesLoader.iterator());
		}

		for (ClassesLoader classesLoader : serviceList) {
			iterators.add(classesLoader.iterator());
		}

		Iterator<Class<?>> iterator = new MultiIterator<Class<?>>(iterators);
		return new DuplicateRemovalIterator<Class<?>>(iterator);
	}
}
