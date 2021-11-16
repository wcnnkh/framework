package io.basc.framework.rpc.http;

import java.lang.reflect.Method;
import java.net.URI;

import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.factory.ConfigurableServices;

public class HttpRemoteResolvers extends
		ConfigurableServices<HttpRemoteResolver> implements
		HttpRemoteResolver, EnvironmentAware {
	private Environment environment;

	public HttpRemoteResolvers() {
		super(HttpRemoteResolver.class);
	}

	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	protected void aware(HttpRemoteResolver service) {
		if (environment != null && service instanceof EnvironmentAware) {
			((EnvironmentAware) service).setEnvironment(environment);
		}
		super.aware(service);
	}

	@Override
	public boolean canResolve(Class<?> clazz) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public URI resolve(Class<?> clazz) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(clazz)) {
				return resolver.resolve(clazz);
			}
		}
		return null;
	}

	@Override
	public boolean canResolve(Method method) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public URI resolve(Method method) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(method)) {
				return resolver.resolve(method);
			}
		}
		return null;
	}

	@Override
	public boolean canResolve(Class<?> clazz, Method method) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(clazz, method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public URI resolve(Class<?> clazz, Method method) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(clazz, method)) {
				return resolver.resolve(clazz, method);
			}
		}
		return null;
	}
}