package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import io.basc.framework.mvc.action.Action;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.security.authority.http.HttpAuthorityManager;
import io.basc.framework.util.Registration;

public class ActionResolverConfiguration implements ActionResolver {

	@Override
	public String getControllerId(Class<?> clazz, Method method) {
		return clazz.getName();
	}

	@Override
	public Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method) {
		return Collections.emptyList();
	}

	@Override
	public Registration registerHttpAuthority(HttpAuthorityManager<? super HttpAuthority> httpAuthorityManager,
			Action action) {
		return Registration.EMPTY;
	}

}
