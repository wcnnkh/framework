package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

public interface ActionResolverExtend {
	default String getControllerId(Class<?> sourceClass, Method method, ActionResolver chain) {
		return chain.getControllerId(sourceClass, method);
	}

	default Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method, ActionResolver chain) {
		return chain.getActionInterceptorNames(sourceClass, method);
	}
}
