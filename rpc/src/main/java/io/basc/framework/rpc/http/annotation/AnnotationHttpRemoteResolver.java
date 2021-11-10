package io.basc.framework.rpc.http.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.net.URI;

import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.rpc.http.HttpRemoteResolver;

public class AnnotationHttpRemoteResolver implements HttpRemoteResolver,
		EnvironmentAware {
	private Environment environment;

	@Override
	public boolean canResolve(Class<?> clazz) {
		return clazz.isAnnotationPresent(HttpRemote.class);
	}

	protected URI resolveByAnnotation(AnnotatedElement annotatedElement) {
		HttpRemote httpRemote = annotatedElement
				.getAnnotation(HttpRemote.class);
		String url = httpRemote.value();
		if (environment != null) {
			url = environment.resolvePlaceholders(url);
		}
		return UriUtils.toUri(url);
	}

	@Override
	public URI resolve(Class<?> clazz) {
		return resolveByAnnotation(clazz);
	}

	@Override
	public boolean canResolve(Method method) {
		return method.isAnnotationPresent(HttpRemote.class);
	}

	@Override
	public URI resolve(Method method) {
		return resolveByAnnotation(method);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}
