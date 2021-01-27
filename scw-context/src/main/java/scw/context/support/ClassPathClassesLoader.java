package scw.context.support;

import scw.io.ResourcePatternResolver;

public class ClassPathClassesLoader<S> extends ResourcePatternClassesLoader<S> {
	public ClassPathClassesLoader(String locationPattern) {
		super(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ locationPattern);
	}
}
