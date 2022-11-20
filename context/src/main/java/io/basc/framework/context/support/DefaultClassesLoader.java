package io.basc.framework.context.support;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ConfigurableClassesLoader;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.Cursors;

public class DefaultClassesLoader implements ConfigurableClassesLoader {
	private final Set<Class<?>> defaultClasses = new LinkedHashSet<Class<?>>();
	private Predicate<Class<?>> filter;
	private final ConfigurableServices<ClassesLoader> classesLoaders = new ConfigurableServices<>(ClassesLoader.class);

	public DefaultClassesLoader() {
	}

	public DefaultClassesLoader(@Nullable Predicate<Class<?>> filter) {
		this.filter = filter;
	}

	public void add(Class<?> clazz) {
		if (filter != null && !filter.test(clazz)) {
			return;
		}
		defaultClasses.add(clazz);
	}

	public void add(ClassesLoader classesLoader) {
		classesLoaders.addService(filter == null ? classesLoader : classesLoader.filter(filter));
	}

	public Set<Class<?>> getDefaultClasses() {
		return defaultClasses;
	}

	public ConfigurableServices<ClassesLoader> getClassesLoaders() {
		return classesLoaders;
	}

	public void reload() {
		for (ClassesLoader classesLoader : classesLoaders) {
			classesLoader.reload();
		}
	}

	@SuppressWarnings("resource")
	public Cursor<Class<?>> iterator() {
		List<Cursor<Class<?>>> iterators = new ArrayList<Cursor<Class<?>>>();
		iterators.add(Cursor.create(defaultClasses.iterator()));
		for (ClassesLoader classesLoader : classesLoaders) {
			iterators.add(classesLoader.iterator());
		}
		return new Cursors<Class<?>>(iterators).flatConvert((e) -> e.distinct());
	}
}
