package scw.core.type.scanner;

import java.util.Set;

import scw.core.type.filter.TypeFilter;
import scw.io.ResourcePatternResolver;

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
