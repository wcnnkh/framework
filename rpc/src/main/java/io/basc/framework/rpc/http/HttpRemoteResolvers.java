package io.basc.framework.rpc.http;

import java.lang.reflect.Method;
import java.net.URI;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;

public class HttpRemoteResolvers extends ConfigurableServices<HttpRemoteResolver>
		implements HttpRemoteResolver, EnvironmentAware {
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
	public boolean canResolve(Class<?> clazz) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public URI resolve(Class<?> clazz, Environment environment) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(clazz)) {
				return resolver.resolve(clazz, environment);
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
	public URI resolve(Method method, Environment environment) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(method)) {
				return resolver.resolve(method, environment);
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
	public URI resolve(Class<?> clazz, Method method, Environment environment) {
		for (HttpRemoteResolver resolver : this) {
			if (resolver.canResolve(clazz, method)) {
				return resolver.resolve(clazz, method, environment);
			}
		}
		return null;
	}
}
