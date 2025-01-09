package io.basc.framework.web.pattern;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import io.basc.framework.core.env.SystemProperties;
import io.basc.framework.util.collection.CollectionUtils;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderFormatAware;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractHttpPatternResolver implements HttpPatternResolver, PlaceholderFormatAware {
	@NonNull
	private PlaceholderFormat placeholderFormat = SystemProperties.getInstance();

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
