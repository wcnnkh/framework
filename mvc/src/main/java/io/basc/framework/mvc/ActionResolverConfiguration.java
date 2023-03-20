package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

public class ActionResolverConfiguration implements ActionResolver {

	@Override
	public String getControllerId(Class<?> clazz, Method method) {
		return clazz.getName();
	}

	@Override
	public Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method) {
		return Collections.emptyList();
	}

}
