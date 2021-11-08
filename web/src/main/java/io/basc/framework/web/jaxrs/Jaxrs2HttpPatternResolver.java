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

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.web.pattern.AbstractHttpPatternResolver;
import io.basc.framework.web.pattern.HttpPattern;

@Provider
public class Jaxrs2HttpPatternResolver extends AbstractHttpPatternResolver {

	@Override
	public boolean canResolve(Class<?> clazz) {
		return AnnotatedElementUtils.isAnnotated(clazz, Path.class);
	}

	@Override
	public boolean canResolve(Method method) {
		return AnnotatedElementUtils.isAnnotated(method, Path.class);
	}

	protected Collection<HttpPattern> resolve(AnnotatedElement annotatedElement) {
		Path clazzPath = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Path.class);
		Set<HttpMethod> httpMethods = AnnotatedElementUtils.getAllMergedAnnotations(annotatedElement, HttpMethod.class);
		Consumes consumes = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Consumes.class);
		Produces produces = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Produces.class);
		MimeTypes consumeTypes = new MimeTypes(consumes.value());
		MimeTypes produceTypes = new MimeTypes(produces.value());
		String path = clazzPath.value();

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
		return resolve(clazz);
	}

	@Override
	protected Collection<HttpPattern> resolveInternal(Method method) {
		return resolve(method);
	}
}
