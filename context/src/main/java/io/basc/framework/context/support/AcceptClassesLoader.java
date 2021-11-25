package io.basc.framework.context.support;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;

public class AcceptClassesLoader implements ClassesLoader {
	private final ClassesLoader classesLoader;
	private final Accept<Class<?>> accept;
	private final boolean cache;
	private volatile List<Class<?>> cacheClasses;

	public AcceptClassesLoader(ClassesLoader classesLoader, @Nullable Accept<Class<?>> accept) {
		this(classesLoader, accept, false);
	}

	public AcceptClassesLoader(ClassesLoader classesLoader, @Nullable Accept<Class<?>> accept, boolean cache) {
		this.classesLoader = classesLoader;
		this.accept = accept;
		this.cache = cache;
	}

	private List<Class<?>> filter() {
		if (accept == null) {
			return classesLoader.toList();
		} else {
			return classesLoader.stream().filter(accept).collect(Collectors.toList());
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
			if (accept == null) {
				return classesLoader.iterator();
			} else {
				return classesLoader.stream().filter(accept).iterator();
			}
		}
	}
}
