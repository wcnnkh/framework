package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

import io.basc.framework.mvc.action.Action;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.security.authority.http.HttpAuthorityManager;
import io.basc.framework.util.register.Registration;

public interface ActionResolverExtend {
	default String getControllerId(Class<?> sourceClass, Method method, ActionResolver chain) {
		return chain.getControllerId(sourceClass, method);
	}

	default Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method, ActionResolver chain) {
		return chain.getActionInterceptorNames(sourceClass, method);
	}

	default Registration registerHttpAuthority(HttpAuthorityManager<? super HttpAuthority> httpAuthorityManager,
			Action action, ActionResolver chain) {
		return chain.registerHttpAuthority(httpAuthorityManager, action);
	}
}
