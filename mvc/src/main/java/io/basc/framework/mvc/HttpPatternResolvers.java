package io.basc.framework.mvc;

import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.mvc.annotation.AnnotationHttpPatternResolver;
import io.basc.framework.util.placeholder.PropertyResolverAware;
import io.basc.framework.web.pattern.HttpPattern;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HttpPatternResolvers extends AnnotationHttpPatternResolver
		implements HttpPatternResolver {
	private List<HttpPatternResolver> resolvers = new ArrayList<HttpPatternResolver>(
			8);

	public HttpPatternResolvers() {
	}

	public HttpPatternResolvers(ServiceLoaderFactory serviceLoaderFactory) {
		resolvers.addAll(serviceLoaderFactory.getServiceLoader(
				HttpPatternResolver.class).toList());
	}

	public void addResolve(HttpPatternResolver resolver) {
		synchronized (resolvers) {
			if (resolver instanceof PropertyResolverAware) {
				((PropertyResolverAware) resolver)
						.setPropertyResolver(getPropertyResolver());
			}
			resolvers.add(resolver);
		}
	}
	
	@Override
	public boolean canResolveHttpPattern(Class<?> clazz) {
		for (HttpPatternResolver resolver : resolvers) {
			if (resolver.canResolveHttpPattern(clazz)) {
				return true;
			}
		}
		return super.canResolveHttpPattern(clazz);
	}

	@Override
	public boolean canResolveHttpPattern(Class<?> clazz, Method method) {
		for (HttpPatternResolver resolver : resolvers) {
			if (resolver.canResolveHttpPattern(clazz, method)) {
				return true;
			}
		}
		return super.canResolveHttpPattern(clazz, method);
	}
	
	@Override
	public Collection<HttpPattern> resolveHttpPattern(Class<?> clazz,
			Method method) {
		Set<HttpPattern> patterns = new LinkedHashSet<HttpPattern>();
		for (HttpPatternResolver resolver : resolvers) {
			if (resolver.canResolveHttpPattern(clazz, method)) {
				patterns.addAll(resolver.resolveHttpPattern(clazz, method));
			}
		}

		if (super.canResolveHttpPattern(clazz, method)) {
			patterns.addAll(super.resolveHttpPattern(clazz, method));
		}
		return patterns;
	}

}
