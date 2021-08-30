package io.basc.framework.mvc;

import io.basc.framework.web.pattern.HttpPattern;

import java.lang.reflect.Method;
import java.util.Collection;

public interface HttpPatternResolver {
	boolean canResolveHttpPattern(Class<?> clazz);
	
	boolean canResolveHttpPattern(Class<?> clazz, Method method);

	Collection<HttpPattern> resolveHttpPattern(Class<?> clazz, Method method);
}
