package io.basc.framework.util;

import java.util.function.Function;

public class StringToClass extends DefaultClassLoaderAccessor implements Function<String, Class<?>> {
	public static final StringToClass DEFAULT = new StringToClass();

	@Override
	public Class<?> apply(String source) {
		return StringUtils.hasText(source) ? ClassUtils.resolveClassName(source, getClassLoader()) : null;
	}

}
