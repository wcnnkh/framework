package io.basc.framework.web.pattern;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.util.StringUtils;

public interface HttpPatternResolver {
	boolean canResolve(Class<?> clazz);

	Collection<HttpPattern> resolve(Class<?> clazz);

	boolean canResolve(Method method);

	Collection<HttpPattern> resolve(Method method);

	default boolean canResolve(Class<?> clazz, Method method) {
		return canResolve(clazz) && canResolve(method);
	}

	default Collection<HttpPattern> resolve(Class<?> clazz, Method method) {
		Collection<HttpPattern> clazzPatterns = resolve(clazz);
		Collection<HttpPattern> methodPatterns = resolve(method);

		Collection<HttpPattern> patterns = new LinkedHashSet<>(clazzPatterns.size() + methodPatterns.size());
		for (HttpPattern clazzPattern : clazzPatterns) {
			for (HttpPattern methodPattern : methodPatterns) {
				String path = StringUtils.mergePaths("/", clazzPattern.getPath(), methodPattern.getPath());

				String httpMethod = methodPattern.getMethod();
				if (!StringUtils.hasText(httpMethod)) {
					httpMethod = clazzPattern.getMethod();
				}

				if (!StringUtils.hasText(httpMethod)) {
					// 默认使用GET
					httpMethod = HttpMethod.GET.name();
				}

				MimeTypes consumes = new MimeTypes();
				consumes.addAll(clazzPattern.getConsumes());
				consumes.addAll(methodPattern.getConsumes());

				MimeTypes produces = new MimeTypes();
				produces.addAll(clazzPattern.getProduces());
				produces.addAll(methodPattern.getProduces());
				HttpPattern httpPattern = new HttpPattern(path, httpMethod, consumes.isEmpty() ? null : consumes,
						produces.isEmpty() ? null : produces);
				patterns.add(httpPattern);
			}
		}
		return patterns;
	}
}