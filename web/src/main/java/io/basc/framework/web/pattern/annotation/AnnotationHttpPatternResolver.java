package io.basc.framework.web.pattern.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.pattern.AbstractHttpPatternResolver;
import io.basc.framework.web.pattern.HttpPattern;

public class AnnotationHttpPatternResolver extends AbstractHttpPatternResolver {

	@Override
	public boolean canResolve(Class<?> clazz) {
		return AnnotatedElementUtils.hasAnnotation(clazz, RequestMapping.class);
	}

	@Override
	public boolean canResolve(Method method) {
		return AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class);
	}

	protected Collection<HttpPattern> resolveByAnnotation(AnnotatedElement annotatedElement) {
		RequestMapping requestMapping = AnnotatedElementUtils.getMergedAnnotation(annotatedElement,
				RequestMapping.class);
		if (requestMapping == null) {
			return Collections.emptyList();
		}

		MimeTypes consumes = new MimeTypes(requestMapping.consumes());
		MimeTypes produces = new MimeTypes(requestMapping.produces());
		String path = StringUtils.cleanPath(requestMapping.value());
		HttpMethod[] methods = requestMapping.methods();
		if (ArrayUtils.isEmpty(methods)) {
			return Arrays.asList(new HttpPattern(path, null, consumes, produces));
		}

		List<HttpPattern> patterns = new ArrayList<>(methods.length);
		for (HttpMethod httpMethod : methods) {
			patterns.add(new HttpPattern(path, httpMethod.name(), consumes, produces));
		}
		return patterns;
	}

	@Override
	protected Collection<HttpPattern> resolveInternal(Method method) {
		return resolveByAnnotation(method);
	}

	@Override
	protected Collection<HttpPattern> resolveInternal(Class<?> clazz) {
		return resolveByAnnotation(clazz);
	}

}
