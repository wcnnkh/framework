package io.basc.framework.web.pattern;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.placeholder.PropertyResolver;
import io.basc.framework.util.placeholder.PropertyResolverAware;

public class HttpPatternResolvers extends ConfigurableServices<HttpPatternResolver>
		implements HttpPatternResolver, PropertyResolverAware {
	private PropertyResolver propertyResolver;

	public HttpPatternResolvers() {
		super(HttpPatternResolver.class);
	}

	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}

	@Override
	protected void aware(HttpPatternResolver service) {
		if (service instanceof PropertyResolverAware) {
			((PropertyResolverAware) service).setPropertyResolver(propertyResolver);
		}
		super.aware(service);
	}

	@Override
	public boolean canResolve(Class<?> clazz) {
		for (HttpPatternResolver resolver : this) {
			if (resolver.canResolve(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canResolve(Class<?> clazz, Method method) {
		for (HttpPatternResolver resolver : this) {
			if (resolver.canResolve(clazz, method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<HttpPattern> resolve(Class<?> clazz, Method method) {
		Set<HttpPattern> patterns = new LinkedHashSet<HttpPattern>(8);
		for (HttpPatternResolver resolver : this) {
			if (resolver.canResolve(clazz, method)) {
				Collection<HttpPattern> ps = resolver.resolve(clazz, method);
				if (CollectionUtils.isEmpty(ps)) {
					continue;
				}
				patterns.addAll(ps);
			}
		}
		return patterns;
	}
}
