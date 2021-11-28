package io.basc.framework.core.type.scanner;

import java.util.Collection;

import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.ResourcePatternResolver;

public class ClassPathClassScanner extends ResourcePatternClassScanner {
	public static ClassPathClassScanner INSTANCE = new ClassPathClassScanner();
	
	@Override
	protected Collection<Resource> getResources(String packageName, ResourceLoader resourceLoader, ClassLoader classLoader) {
		return super.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + packageName, resourceLoader, classLoader);
	}
}
