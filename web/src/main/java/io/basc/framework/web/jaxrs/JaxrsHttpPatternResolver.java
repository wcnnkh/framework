package io.basc.framework.web.jaxrs;

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
import io.basc.framework.http.HttpPattern;
import io.basc.framework.net.MediaTypes;
import io.basc.framework.net.pattern.RequestPatternFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;

public class JaxrsHttpPatternResolver implements RequestPatternFactory {

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
			HttpPattern pattern = new HttpPattern();
			pattern.setPath(path);
			pattern.getConsumes().registers(consumeTypes);
			pattern.getProduces().registers(produceTypes);
			return Arrays.asList(pattern);
		}

		Set<HttpPattern> httpPatterns = new LinkedHashSet<HttpPattern>();
		for (HttpMethod httpMethod : httpMethods) {
			HttpPattern pattern = new HttpPattern();
			pattern.setPath(path);
			pattern.setMethod(httpMethod.value());
			pattern.getConsumes().registers(consumeTypes);
			pattern.getProduces().registers(produceTypes);
			httpPatterns.add(pattern);
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
