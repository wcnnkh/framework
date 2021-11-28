package io.basc.framework.core.type.scanner;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import io.basc.framework.core.type.filter.TypeFilter;

public class DefaultClassScanner implements ConfigurableClassScanner {
	protected final List<ClassScanner> scanners = new LinkedList<ClassScanner>();

	public List<ClassScanner> getScanners() {
		return scanners;
	}

	public void addClassScanner(ClassScanner classScanner) {
		getScanners().add(classScanner);
	}

	@Override
	public void scan(String packageName, ClassLoader classLoader, TypeFilter typeFilter,
			Predicate<Class<?>> predicate) {
		for (ClassScanner scanner : scanners) {
			scanner.scan(packageName, classLoader, typeFilter, predicate);
		}
		ClassPathClassScanner.INSTANCE.scan(packageName, classLoader, typeFilter, predicate);
	}
}
