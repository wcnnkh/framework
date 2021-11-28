package io.basc.framework.core.type.scanner;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;

public interface ClassScanner {
	
	void scan(String packageName, @Nullable ClassLoader classLoader, @Nullable TypeFilter typeFilter, Predicate<Class<?>> predicate);
	
	default void scan(Collection<Class<?>> outputs, String packageName, @Nullable ClassLoader classLoader, @Nullable TypeFilter typeFilter, @Nullable Predicate<Class<?>> predicate) {
		scan(packageName, classLoader, typeFilter, (c) -> {
			if(predicate.test(c)) {
				outputs.add(c);
			}
			return true;
		});
	}
	
	default Set<Class<?>> getClasses(String packageName, @Nullable ClassLoader classLoader, @Nullable TypeFilter typeFilter, @Nullable Predicate<Class<?>> predicate){
		Set<Class<?>> ouptuts = new LinkedHashSet<Class<?>>();
		scan(ouptuts, packageName, classLoader, typeFilter, predicate);
		return ouptuts;
	}
	
	default Set<Class<?>> getClasses(String packageName, @Nullable ClassLoader classLoader, @Nullable TypeFilter typeFilter){
		return getClasses(packageName, classLoader, typeFilter, null);
	}
}
