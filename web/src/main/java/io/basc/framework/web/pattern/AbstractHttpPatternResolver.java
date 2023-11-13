package io.basc.framework.web.pattern;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import io.basc.framework.env.Sys;
import io.basc.framework.text.placeholder.PlaceholderFormat;
import io.basc.framework.text.placeholder.PlaceholderFormatAware;
import io.basc.framework.util.CollectionUtils;

public abstract class AbstractHttpPatternResolver implements HttpPatternResolver, PlaceholderFormatAware {
	private PlaceholderFormat placeholderFormat;

	@Override
	public void setPlaceholderFormat(PlaceholderFormat placeholderFormat) {
		this.placeholderFormat = placeholderFormat;
	}

	public PlaceholderFormat getPlaceholderFormat() {
		return placeholderFormat == null ? Sys.getEnv().getProperties() : placeholderFormat;
	}

	@Override
	public Collection<HttpPattern> resolve(Method method) {
		Collection<HttpPattern> sourcePatterns = resolveInternal(method);
		if (CollectionUtils.isEmpty(sourcePatterns)) {
			return Collections.emptyList();
		}

		return sourcePatterns.stream().map((pattern) -> {
			return new HttpPattern(getPlaceholderFormat().replacePlaceholders(pattern.getPath()), pattern.getMethod(),
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
			return new HttpPattern(getPlaceholderFormat().replacePlaceholders(pattern.getPath()), pattern.getMethod(),
					pattern.getConsumes(), pattern.getProduces());
		}).collect(Collectors.toList());
	}

	protected abstract Collection<HttpPattern> resolveInternal(Class<?> clazz);
}
