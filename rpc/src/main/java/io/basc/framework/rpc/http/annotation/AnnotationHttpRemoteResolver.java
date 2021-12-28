package io.basc.framework.rpc.http.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.net.URI;

import io.basc.framework.env.Environment;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.rpc.http.HttpRemoteResolver;

public class AnnotationHttpRemoteResolver implements HttpRemoteResolver {

	@Override
	public boolean canResolve(Class<?> clazz) {
		return clazz.isAnnotationPresent(HttpRemote.class);
	}

	protected URI resolveByAnnotation(AnnotatedElement annotatedElement, Environment environment) {
		HttpRemote httpRemote = annotatedElement.getAnnotation(HttpRemote.class);
		String url = httpRemote.value();
		if (environment != null) {
			url = environment.resolvePlaceholders(url);
		}
		return UriUtils.toUri(url);
	}

	@Override
	public URI resolve(Class<?> clazz, Environment environment) {
		return resolveByAnnotation(clazz, environment);
	}

	@Override
	public boolean canResolve(Method method) {
		return method.isAnnotationPresent(HttpRemote.class);
	}

	@Override
	public URI resolve(Method method, Environment environment) {
		return resolveByAnnotation(method, environment);
	}
}
