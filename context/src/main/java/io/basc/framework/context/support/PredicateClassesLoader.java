package io.basc.framework.context.support;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.lang.Nullable;

public class PredicateClassesLoader implements ClassesLoader {
	private final ClassesLoader classesLoader;
	private final Predicate<Class<?>> filter;
	private final boolean cache;
	private volatile List<Class<?>> cacheClasses;

	public PredicateClassesLoader(ClassesLoader classesLoader, @Nullable Predicate<Class<?>> filter) {
		this(classesLoader, filter, false);
	}

	public PredicateClassesLoader(ClassesLoader classesLoader, @Nullable Predicate<Class<?>> filter, boolean cache) {
		this.classesLoader = classesLoader;
		this.filter = filter;
		this.cache = cache;
	}

	private List<Class<?>> filter() {
		if (filter == null) {
			return classesLoader.toList();
		} else {
			return classesLoader.stream().filter(filter).collect(Collectors.toList());
		}
	}

	public void reload() {
		if (cache) {
			synchronized (this) {
				cacheClasses = filter();
			}
		} else {
			classesLoader.reload();
		}
	}

	public Iterator<Class<?>> iterator() {
		if (cache) {
			if (cacheClasses == null) {
				synchronized (this) {
					if (cacheClasses == null) {
						cacheClasses = filter();
					}
				}
			}
			return cacheClasses.iterator();
		} else {
			if (filter == null) {
				return classesLoader.iterator();
			} else {
				return classesLoader.stream().filter(filter).iterator();
			}
		}
	}
}
