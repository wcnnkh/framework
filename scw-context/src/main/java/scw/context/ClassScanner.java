package scw.context;

import java.util.Set;

import scw.core.type.filter.TypeFilter;
import scw.lang.Nullable;

public interface ClassScanner {
	Set<Class<?>> getClasses(String packageName,
			@Nullable ClassLoader classLoader, @Nullable TypeFilter typeFilter);
}
