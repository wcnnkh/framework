package io.basc.framework.core.type.scanner;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;

import java.util.Set;

public interface ClassScanner {
	Set<Class<?>> getClasses(String packageName, @Nullable ClassLoader classLoader, @Nullable TypeFilter typeFilter);
}
