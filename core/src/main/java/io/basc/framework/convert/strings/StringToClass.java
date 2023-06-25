package io.basc.framework.convert.strings;

import java.util.function.Function;

import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.DefaultClassLoaderAccessor;
import io.basc.framework.util.StringUtils;

public class StringToClass extends DefaultClassLoaderAccessor implements Function<String, Class<?>> {
	public static final StringToClass DEFAULT = new StringToClass();

	@Override
	public Class<?> apply(String source) {
		return StringUtils.hasText(source) ? ClassUtils.resolveClassName(source, getClassLoader()) : null;
	}

}
