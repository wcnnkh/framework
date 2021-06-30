package scw.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.web.pattern.HttpPattern;

public interface HttpPatternResolver {
	boolean canResolveHttpPattern(Class<?> clazz);
	
	boolean canResolveHttpPattern(Class<?> clazz, Method method);

	Collection<HttpPattern> resolveHttpPattern(Class<?> clazz, Method method);
}
