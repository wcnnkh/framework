package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.mvc.annotation.AnnotationHttpPatternResolver;
import io.basc.framework.util.placeholder.PropertyResolver;
import io.basc.framework.util.placeholder.PropertyResolverAware;
import io.basc.framework.web.pattern.HttpPattern;

public class HttpPatternResolvers extends ConfigurableServices<HttpPatternResolver> implements HttpPatternResolver, PropertyResolverAware {
	private final AnnotationHttpPatternResolver annotationHttpPatternResolver = new AnnotationHttpPatternResolver();
	private PropertyResolver propertyResolver;
	
	public HttpPatternResolvers() {
		super(HttpPatternResolver.class);
	}
	
	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
		annotationHttpPatternResolver.setPropertyResolver(propertyResolver);
	}
	
	@Override
	protected void aware(HttpPatternResolver service) {
		if(service instanceof PropertyResolverAware) {
			((PropertyResolverAware) service).setPropertyResolver(propertyResolver);
		}
		super.aware(service);
	}
	
	@Override
	public boolean canResolveHttpPattern(Class<?> clazz) {
		for (HttpPatternResolver resolver : this) {
			if (resolver.canResolveHttpPattern(clazz)) {
				return true;
			}
		}
		return annotationHttpPatternResolver.canResolveHttpPattern(clazz);
	}

	@Override
	public boolean canResolveHttpPattern(Class<?> clazz, Method method) {
		for (HttpPatternResolver resolver : this) {
			if (resolver.canResolveHttpPattern(clazz, method)) {
				return true;
			}
		}
		return annotationHttpPatternResolver.canResolveHttpPattern(clazz, method);
	}
	
	@Override
	public Collection<HttpPattern> resolveHttpPattern(Class<?> clazz,
			Method method) {
		Set<HttpPattern> patterns = new LinkedHashSet<HttpPattern>();
		for (HttpPatternResolver resolver : this) {
			if (resolver.canResolveHttpPattern(clazz, method)) {
				patterns.addAll(resolver.resolveHttpPattern(clazz, method));
			}
		}

		if (annotationHttpPatternResolver.canResolveHttpPattern(clazz, method)) {
			patterns.addAll(annotationHttpPatternResolver.resolveHttpPattern(clazz, method));
		}
		return patterns;
	}

}
