package io.basc.framework.type.scanner;

import io.basc.framework.lang.Nullable;
import io.basc.framework.type.filter.TypeFilter;

import java.util.Set;

public interface ClassScanner {
	Set<Class<?>> getClasses(String packageName,
			@Nullable ClassLoader classLoader, @Nullable TypeFilter typeFilter);
}
