package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.security.authority.http.HttpAuthorityManager;
import io.basc.framework.util.Registration;

@Provider(value = ActionResolver.class)
public class ConfigurableActiontResolver extends ConfigurableServices<ActionResolverExtend> implements ActionResolver {
	private ActionResolver defaultResolver;

	public ConfigurableActiontResolver() {
		super(ActionResolverExtend.class);
	}

	public ActionResolver getDefaultResolver() {
		return defaultResolver;
	}

	public void setDefaultResolver(ActionResolver defaultResolver) {
		this.defaultResolver = defaultResolver;
	}

	@Override
	public String getControllerId(Class<?> clazz, Method method) {
		return ActionResolverChain.build(iterator(), getDefaultResolver()).getControllerId(clazz, method);
	}

	@Override
	public Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method) {
		return ActionResolverChain.build(iterator(), getDefaultResolver()).getActionInterceptorNames(sourceClass,
				method);
	}

	@Override
	public Registration registerHttpAuthority(HttpAuthorityManager<? super HttpAuthority> httpAuthorityManager,
			Action action) {
		return ActionResolverChain.build(iterator(), getDefaultResolver()).registerHttpAuthority(httpAuthorityManager,
				action);
	}
}
