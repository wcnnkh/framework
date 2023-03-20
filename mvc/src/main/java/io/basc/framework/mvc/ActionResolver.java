package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

public interface ActionResolver {
	String getControllerId(Class<?> sourceClass, Method method);

	Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method);
}
