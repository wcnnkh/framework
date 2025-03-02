package io.basc.framework.http.jaxrs;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.net.MediaTypes;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.web.pattern.AbstractHttpPatternResolver;
import io.basc.framework.web.pattern.HttpPattern;

public class JaxrsHttpPatternResolver extends AbstractHttpPatternResolver {

	@Override
	public boolean canResolve(Class<?> clazz) {
		return AnnotatedElementUtils.hasAnnotation(clazz, Path.class);
	}

	@Override
	public boolean canResolve(Method method) {
		return AnnotatedElementUtils.hasAnnotation(method, Path.class);
	}

	protected Collection<HttpPattern> resolveByAnnotation(AnnotatedElement annotatedElement) {
		Path pathAnnotation = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Path.class);
		Set<HttpMethod> httpMethods = AnnotatedElementUtils.getAllMergedAnnotations(annotatedElement, HttpMethod.class);
		Consumes consumes = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Consumes.class);
		Produces produces = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Produces.class);
		MediaTypes consumeTypes = MediaTypes.forArray(consumes.value());
		MediaTypes produceTypes = MediaTypes.forArray(produces.value());
		String path = StringUtils.cleanPath(pathAnnotation.value());
		if (CollectionUtils.isEmpty(httpMethods)) {
			return Arrays.asList(new HttpPattern(path, null, consumeTypes, produceTypes));
		}

		Set<HttpPattern> httpPatterns = new LinkedHashSet<HttpPattern>();
		for (HttpMethod httpMethod : httpMethods) {
			httpPatterns.add(new HttpPattern(path, httpMethod.value(), consumeTypes, produceTypes));
		}
		return httpPatterns;
	}

	@Override
	protected Collection<HttpPattern> resolveInternal(Class<?> clazz) {
		return resolveByAnnotation(clazz);
	}

	@Override
	protected Collection<HttpPattern> resolveInternal(Method method) {
		return resolveByAnnotation(method);
	}
}
