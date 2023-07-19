package io.basc.framework.web.pattern;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.env.Sys;
import io.basc.framework.util.Registration;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderFormatAware;

public class HttpPatternResolvers extends ConfigurableServices<HttpPatternResolver>
		implements HttpPatternResolver, PlaceholderFormatAware {
	private PlaceholderFormat placeholderFormat;

	public HttpPatternResolvers() {
		super(HttpPatternResolver.class);
		getServiceInjectors().register((service) -> {
			if (placeholderFormat != null) {
				if (service instanceof PlaceholderFormatAware) {
					((PlaceholderFormatAware) service).setPlaceholderFormat(placeholderFormat);
				}
			}
			return Registration.EMPTY;
		});
	}

	public PlaceholderFormat getPlaceholderFormat() {
		return placeholderFormat == null ? Sys.getEnv().getProperties() : placeholderFormat;
	}

	public void setPlaceholderFormat(PlaceholderFormat placeholderFormat) {
		this.placeholderFormat = placeholderFormat;
	}

	@Override
	public boolean canResolve(Class<?> clazz) {
		for (HttpPatternResolver resolver : getServices()) {
			if (resolver.canResolve(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<HttpPattern> resolve(Class<?> clazz) {
		Collection<HttpPattern> patterns = new LinkedHashSet<>(8);
		for (HttpPatternResolver resolver : getServices()) {
			if (resolver.canResolve(clazz)) {
				patterns.addAll(resolver.resolve(clazz));
			}
		}
		return patterns;
	}

	@Override
	public boolean canResolve(Method method) {
		for (HttpPatternResolver resolver : getServices()) {
			if (resolver.canResolve(method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<HttpPattern> resolve(Method method) {
		Collection<HttpPattern> patterns = new LinkedHashSet<>(8);
		for (HttpPatternResolver resolver : getServices()) {
			if (resolver.canResolve(method)) {
				patterns.addAll(resolver.resolve(method));
			}
		}
		return patterns;
	}

	@Override
	public boolean canResolve(Class<?> clazz, Method method) {
		for (HttpPatternResolver resolver : getServices()) {
			if (resolver.canResolve(clazz, method)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<HttpPattern> resolve(Class<?> clazz, Method method) {
		Collection<HttpPattern> patterns = new LinkedHashSet<>(8);
		for (HttpPatternResolver resolver : getServices()) {
			if (resolver.canResolve(clazz, method)) {
				patterns.addAll(resolver.resolve(clazz, method));
			}
		}
		return patterns;
	}
}
