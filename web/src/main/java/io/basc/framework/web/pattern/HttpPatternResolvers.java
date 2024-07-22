package io.basc.framework.web.pattern;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.env.SystemProperties;
import io.basc.framework.register.Registration;
import io.basc.framework.text.placeholder.PlaceholderFormat;
import io.basc.framework.text.placeholder.PlaceholderFormatAware;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class HttpPatternResolvers extends ConfigurableServices<HttpPatternResolver>
		implements HttpPatternResolver, PlaceholderFormatAware {
	@NonNull
	private PlaceholderFormat placeholderFormat = SystemProperties.getInstance();

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
