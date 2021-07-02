package scw.context.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.instance.Configurable;
import scw.instance.ServiceList;
import scw.instance.ServiceLoaderFactory;
import scw.lang.Nullable;
import scw.util.Accept;
import scw.util.DuplicateRemovalIterator;
import scw.util.MultiIterator;

public class DefaultClassesLoader implements ConfigurableClassesLoader, Configurable {
	private final List<ClassesLoader> loaders = new LinkedList<ClassesLoader>();
	private final Set<Class<?>> defaultClasses = new LinkedHashSet<Class<?>>();
	private final Accept<Class<?>> accept;
	private final ServiceList<ClassesLoader> serviceList = new ServiceList<>(ClassesLoader.class);

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
