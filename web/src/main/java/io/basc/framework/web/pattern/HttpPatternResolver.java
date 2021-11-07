package io.basc.framework.web.pattern;

import java.lang.reflect.Method;
import java.util.Collection;

public interface HttpPatternResolver {
	boolean canResolve(Class<?> clazz);

	default boolean canResolve(Class<?> clazz, Method method) {
		return canResolve(clazz);
	}

	Collection<HttpPattern> resolve(Class<?> clazz, Method method);
}