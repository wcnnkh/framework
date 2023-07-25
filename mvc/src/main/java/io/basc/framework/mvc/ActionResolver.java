package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

import io.basc.framework.mvc.action.Action;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.security.authority.http.HttpAuthorityManager;
import io.basc.framework.util.registry.Registration;

public interface ActionResolver {
	String getControllerId(Class<?> sourceClass, Method method);

	Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method);

	Registration registerHttpAuthority(HttpAuthorityManager<? super HttpAuthority> httpAuthorityManager, Action action);
}
