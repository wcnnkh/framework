package scw.context.support;

import scw.io.ResourcePatternResolver;

public class ClassPathClassesLoader extends ResourcePatternClassesLoader {
	public ClassPathClassesLoader(String locationPattern) {
		super(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + locationPattern);
	}
}
