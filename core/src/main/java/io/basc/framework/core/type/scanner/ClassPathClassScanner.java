package io.basc.framework.core.type.scanner;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.ResourcePatternResolver;

import java.util.Set;

public class ClassPathClassScanner extends ResourcePatternClassScanner {
	public static ClassPathClassScanner INSTANCE = new ClassPathClassScanner();

	@Override
	public Set<Class<?>> getClasses(String packageName,
			ClassLoader classLoader, TypeFilter typeFilter) {
		return super.getClasses(
				ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + packageName,
				classLoader, typeFilter);
	}
}
