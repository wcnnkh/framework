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
import io.basc.framework.core.execution.Executable;
import io.basc.framework.http.HttpPattern;
import io.basc.framework.net.MediaTypes;
import io.basc.framework.net.RequestPattern;
import io.basc.framework.net.call.RequestPatternResolver;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.Elements;

public class JaxrsHttpPatternResolver implements RequestPatternResolver {

	@Override
	public boolean canResolve(Executable executable) {
		return executable.isAnnotated(Path.class.getName());
	}
	
	@Override
	public Elements<RequestPattern> resolveRequestPatterns(Executable executable, Object... args) {
		Collection<HttpPattern> rootPatterns = resolveByAnnotation(executable.getDeclaringTypeDescriptor().getType());
		Collection<HttpPattern> rootPatterns = resolveByAnnotation(executable.getAnnotations());
		return null;
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
