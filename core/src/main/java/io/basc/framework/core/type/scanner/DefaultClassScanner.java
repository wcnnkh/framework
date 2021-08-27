package io.basc.framework.core.type.scanner;

import io.basc.framework.core.type.filter.TypeFilter;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DefaultClassScanner implements
		ConfigurableClassScanner {
	protected final List<ClassScanner> scanners = new LinkedList<ClassScanner>();

	public List<ClassScanner> getScanners() {
		return scanners;
	}

	public void addClassScanner(ClassScanner classScanner) {
		getScanners().add(classScanner);
	}

	public Set<Class<?>> getClasses(String packageName,
			ClassLoader classLoader, TypeFilter typeFilter) {
		Set<Class<?>> all = new LinkedHashSet<Class<?>>();
		for (ClassScanner scanner : scanners) {
			Set<Class<?>> classes = scanner.getClasses(packageName,
					classLoader, typeFilter);
			if (classes != null) {
				all.addAll(classes);
			}
		}
		Set<Class<?>> classes = ClassPathClassScanner.INSTANCE.getClasses(
				packageName, classLoader, typeFilter);
		if (classes != null) {
			all.addAll(classes);
		}
		return all;
	}
}
