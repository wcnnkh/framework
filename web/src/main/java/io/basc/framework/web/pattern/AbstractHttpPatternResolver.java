package io.basc.framework.web.pattern;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import io.basc.framework.env.Sys;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.placeholder.PropertyResolver;
import io.basc.framework.util.placeholder.PropertyResolverAware;

public abstract class AbstractHttpPatternResolver implements HttpPatternResolver, PropertyResolverAware {
	private PropertyResolver propertyResolver;

	@Override
	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}

	public PropertyResolver getPropertyResolver() {
		return propertyResolver == null ? Sys.env : propertyResolver;
	}

	@Override
	public Collection<HttpPattern> resolve(Method method) {
		Collection<HttpPattern> sourcePatterns = resolveInternal(method);
		if (CollectionUtils.isEmpty(sourcePatterns)) {
			return Collections.emptyList();
		}

		return sourcePatterns.stream().map((pattern) -> {
			return new HttpPattern(getPropertyResolver().resolvePlaceholders(pattern.getPath()), pattern.getMethod(),
					pattern.getConsumes(), pattern.getProduces());
		}).collect(Collectors.toList());
	}

	protected abstract Collection<HttpPattern> resolveInternal(Method method);

	@Override
	public Collection<HttpPattern> resolve(Class<?> clazz) {
		Collection<HttpPattern> sourcePatterns = resolveInternal(clazz);
		if (CollectionUtils.isEmpty(sourcePatterns)) {
			return Collections.emptyList();
		}

		return sourcePatterns.stream().map((pattern) -> {
			return new HttpPattern(getPropertyResolver().resolvePlaceholders(pattern.getPath()), pattern.getMethod(),
					pattern.getConsumes(), pattern.getProduces());
		}).collect(Collectors.toList());
	}

	protected abstract Collection<HttpPattern> resolveInternal(Class<?> clazz);
}
