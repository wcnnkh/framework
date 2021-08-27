package io.basc.framework.context.support;

import io.basc.framework.io.ResourcePatternResolver;

public class ClassPathClassesLoader extends ResourcePatternClassesLoader {
	public ClassPathClassesLoader(String locationPattern) {
		super(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + locationPattern);
	}
}
